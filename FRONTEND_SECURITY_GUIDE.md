# Frontend Security Integration Guide

## 🚀 JWT Security Implementation for Angular Frontend

This guide provides everything the frontend team needs to implement secure authentication with our microservices architecture.

## 📋 Table of Contents

1. [Security Flow Overview](#security-flow-overview)
2. [Angular Service Implementation](#angular-service-implementation)
3. [HTTP Interceptor Setup](#http-interceptor-setup)
4. [Route Guards](#route-guards)
5. [Token Management](#token-management)
6. [API Integration Examples](#api-integration-examples)
7. [Error Handling](#error-handling)
8. [Security Best Practices](#security-best-practices)

## 🔐 Security Flow Overview

### **Authentication Flow:**
```
1. User Login → POST /auth/login
   ↓
2. Server Response → Access Token (15min) + HttpOnly Refresh Token Cookie (7 days)
   ↓
3. Store Access Token → localStorage/sessionStorage
   ↓
4. API Requests → Include Access Token in Authorization header
   ↓
5. Token Expiry → Automatic refresh using HttpOnly cookie
   ↓
6. Logout → POST /auth/logout (clears HttpOnly cookie)
```

### **Token Types:**
- **Access Token**: 15 minutes (sent in Authorization header)
- **Refresh Token**: 7 days (stored in HttpOnly cookie)
- **Security**: HttpOnly cookies prevent XSS attacks

## 🛠️ Angular Service Implementation

### **1. Authentication Service**

```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  email: string;
  role: string;
}

export interface RefreshResponse {
  accessToken: string;
  message: string;
}

export interface LogoutResponse {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_BASE_URL = 'http://localhost:8765/user-management';
  private readonly TOKEN_KEY = 'access_token';
  private readonly USER_KEY = 'current_user';
  
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.initializeAuth();
  }

  /**
   * Initialize authentication state
   */
  private initializeAuth(): void {
    const token = this.getAccessToken();
    const user = this.getCurrentUser();
    
    if (token && user && !this.isTokenExpired(token)) {
      this.isAuthenticatedSubject.next(true);
      this.currentUserSubject.next(user);
    } else {
      this.clearAuthData();
    }
  }

  /**
   * User Login
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_BASE_URL}/auth/login`, credentials, {
      withCredentials: true // Important for HttpOnly cookies
    }).pipe(
      tap(response => {
        this.setAuthData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * User Registration
   */
  register(userData: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_BASE_URL}/auth/register`, userData, {
      withCredentials: true
    }).pipe(
      tap(response => {
        this.setAuthData(response);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Refresh Access Token
   */
  refreshToken(): Observable<RefreshResponse> {
    return this.http.post<RefreshResponse>(`${this.API_BASE_URL}/auth/refresh`, {}, {
      withCredentials: true // Sends refresh token cookie
    }).pipe(
      tap(response => {
        if (response.accessToken) {
          localStorage.setItem(this.TOKEN_KEY, response.accessToken);
        }
      }),
      catchError(error => {
        this.logout();
        return throwError(error);
      })
    );
  }

  /**
   * User Logout
   */
  logout(): Observable<LogoutResponse> {
    return this.http.post<LogoutResponse>(`${this.API_BASE_URL}/auth/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.clearAuthData();
        this.router.navigate(['/login']);
      }),
      catchError(error => {
        this.clearAuthData();
        this.router.navigate(['/login']);
        return throwError(error);
      })
    );
  }

  /**
   * Get Access Token
   */
  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    return token !== null && !this.isTokenExpired(token);
  }

  /**
   * Get current user
   */
  getCurrentUser(): any {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Check if user has specific role
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user && user.role === role;
  }

  /**
   * Check if user has any of the specified roles
   */
  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return user && roles.includes(user.role);
  }

  /**
   * Set authentication data
   */
  private setAuthData(response: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify({
      userId: response.userId,
      email: response.email,
      role: response.role
    }));
    
    this.isAuthenticatedSubject.next(true);
    this.currentUserSubject.next({
      userId: response.userId,
      email: response.email,
      role: response.role
    });
  }

  /**
   * Clear authentication data
   */
  private clearAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  /**
   * Check if token is expired
   */
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      return true;
    }
  }

  /**
   * Handle HTTP errors
   */
  private handleError = (error: any): Observable<never> => {
    console.error('Authentication error:', error);
    return throwError(error);
  };
}
```

### **2. HTTP Interceptor for Automatic Token Handling**

```typescript
// auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add access token to requests (except auth endpoints)
    if (!this.isAuthEndpoint(req.url)) {
      const token = this.authService.getAccessToken();
      if (token) {
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
      }
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !this.isAuthEndpoint(req.url)) {
          return this.handle401Error(req, next);
        }
        return throwError(error);
      })
    );
  }

  private isAuthEndpoint(url: string): boolean {
    return url.includes('/auth/');
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap((response: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(response.accessToken);
          return next.handle(this.addTokenHeader(req, response.accessToken));
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.authService.logout();
          return throwError(error);
        })
      );
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(req, token)))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}
```

### **3. Route Guards**

```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.authService.isAuthenticated$.pipe(
      take(1),
      map(isAuthenticated => {
        if (isAuthenticated) {
          return true;
        } else {
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
          return false;
        }
      })
    );
  }
}
```

```typescript
// role.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const requiredRoles = route.data['roles'] as string[];
    
    return this.authService.currentUser$.pipe(
      take(1),
      map(user => {
        if (user && this.authService.hasAnyRole(requiredRoles)) {
          return true;
        } else {
          this.router.navigate(['/unauthorized']);
          return false;
        }
      })
    );
  }
}
```

### **4. App Module Configuration**

```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AuthService } from './services/auth.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

@NgModule({
  declarations: [
    AppComponent,
    // ... other components
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    AuthGuard,
    RoleGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### **5. Routing Configuration**

```typescript
// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

const routes: Routes = [
  { path: 'login', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  { 
    path: 'dashboard', 
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  { 
    path: 'admin', 
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'shelter', 
    loadChildren: () => import('./shelter/shelter.module').then(m => m.ShelterModule),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SHELTER_MANAGER'] }
  },
  { 
    path: 'animals', 
    loadChildren: () => import('./animals/animals.module').then(m => m.AnimalsModule),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'SHELTER_MANAGER', 'VET'] }
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

## 🔧 API Integration Examples

### **1. Login Component**

```typescript
// login.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const loginRequest: LoginRequest = this.loginForm.value;
      
      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Login failed';
        }
      });
    }
  }
}
```

### **2. API Service for Microservices**

```typescript
// api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly API_BASE_URL = 'http://localhost:8765';

  constructor(private http: HttpClient) {}

  // Animal Service
  getAnimals(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_BASE_URL}/animals-microservice/animals/getAll`);
  }

  getAnimalById(id: string): Observable<any> {
    return this.http.get<any>(`${this.API_BASE_URL}/animals-microservice/animals/getById/${id}`);
  }

  createAnimal(animal: any): Observable<any> {
    return this.http.post<any>(`${this.API_BASE_URL}/animals-microservice/animals`, animal);
  }

  // Shelter Service
  getShelters(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_BASE_URL}/shelter-microservice/shelters/getAll`);
  }

  getShelterAnimals(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_BASE_URL}/shelter-microservice/shelters/getAllAnimals`);
  }

  // User Service
  getUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_BASE_URL}/user-management/users`);
  }

  getUserById(id: string): Observable<any> {
    return this.http.get<any>(`${this.API_BASE_URL}/user-management/users/find/${id}`);
  }
}
```

## 🛡️ Security Best Practices

### **1. Environment Configuration**

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8765',
  userManagementUrl: 'http://localhost:8765/user-management',
  animalsUrl: 'http://localhost:8765/animals-microservice',
  shelterUrl: 'http://localhost:8765/shelter-microservice'
};
```

### **2. Token Storage Security**

```typescript
// token-storage.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly USER_KEY = 'current_user';

  setAccessToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  removeAccessToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  setCurrentUser(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getCurrentUser(): any {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  removeCurrentUser(): void {
    localStorage.removeItem(this.USER_KEY);
  }

  clearAll(): void {
    this.removeAccessToken();
    this.removeCurrentUser();
  }
}
```

### **3. Error Handling Service**

```typescript
// error-handler.service.ts
import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  handleError(error: HttpErrorResponse): void {
    switch (error.status) {
      case 401:
        this.handleUnauthorized();
        break;
      case 403:
        this.handleForbidden();
        break;
      case 404:
        this.handleNotFound();
        break;
      case 500:
        this.handleServerError();
        break;
      default:
        this.handleGenericError();
    }
  }

  private handleUnauthorized(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private handleForbidden(): void {
    this.router.navigate(['/unauthorized']);
  }

  private handleNotFound(): void {
    this.router.navigate(['/not-found']);
  }

  private handleServerError(): void {
    // Show server error message
    console.error('Server error occurred');
  }

  private handleGenericError(): void {
    // Show generic error message
    console.error('An error occurred');
  }
}
```

## 📱 Usage Examples

### **1. Login Flow**

```typescript
// In your login component
login() {
  const credentials = {
    email: 'user@example.com',
    password: 'password123'
  };

  this.authService.login(credentials).subscribe({
    next: (response) => {
      console.log('Login successful:', response);
      // User is automatically redirected to dashboard
    },
    error: (error) => {
      console.error('Login failed:', error);
    }
  });
}
```

### **2. Making Authenticated API Calls**

```typescript
// In your service
getAnimals() {
  // Token is automatically added by interceptor
  return this.http.get('/animals-microservice/animals/getAll');
}
```

### **3. Role-Based Component Access**

```typescript
// In your component
ngOnInit() {
  if (this.authService.hasRole('ADMIN')) {
    // Show admin features
  }
  
  if (this.authService.hasAnyRole(['ADMIN', 'SHELTER_MANAGER'])) {
    // Show shelter management features
  }
}
```

## 🔒 Security Checklist

- ✅ **HttpOnly Cookies**: Refresh tokens stored securely
- ✅ **Automatic Token Refresh**: Seamless user experience
- ✅ **Route Protection**: Guards for authenticated routes
- ✅ **Role-Based Access**: Different access levels
- ✅ **Error Handling**: Proper error management
- ✅ **Token Expiry**: Automatic logout on token expiry
- ✅ **XSS Protection**: Secure token storage
- ✅ **CSRF Protection**: SameSite cookie configuration

## 🚀 Getting Started

1. **Install Dependencies**:
   ```bash
   npm install @angular/common @angular/core @angular/router
   ```

2. **Copy Services**: Add the provided services to your project

3. **Configure Module**: Update your app module with interceptors and guards

4. **Update Routes**: Add route guards to protected routes

5. **Test Integration**: Use the provided examples to test authentication

## 📞 Support

For any questions or issues with the frontend integration, contact the backend team or refer to the main README.md file for additional documentation.

---

**Happy Coding! 🎉**
