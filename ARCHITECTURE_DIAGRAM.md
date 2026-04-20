# Microservices Architecture Diagram

## 🏗️ Complete System Architecture with Enhanced JWT Security

### **Mermaid Diagram Specification**

```mermaid
graph TB
    %% Frontend Layer
    subgraph "Frontend Layer"
        A[Angular Application]
        A1[Auth Service]
        A2[HTTP Interceptor]
        A3[Route Guards]
        A4[API Service]
    end

    %% API Gateway Layer
    subgraph "API Gateway Layer"
        B[Spring Cloud Gateway<br/>Port: 8765]
        B1[JWT Authentication Filter]
        B2[Token Refresh Filter]
        B3[Security Config]
        B4[CORS Configuration]
    end

    %% Service Discovery
    subgraph "Service Discovery"
        C[Netflix Eureka Server<br/>Port: 8761]
    end

    %% Microservices Layer
    subgraph "User Management Service"
        D[User Management<br/>Port: 8091]
        D1[Authentication Controller]
        D2[JWT Token Provider]
        D3[Token Refresh Service]
        D4[Security Config]
        D5[User Service]
        D6[Refresh Token Repository]
    end

    subgraph "Animal Management Service"
        E[Animal Service<br/>Port: 8093/8095]
        E1[Animal Controller]
        E2[Animal Service]
        E3[Animal Repository]
    end

    subgraph "Shelter Management Service"
        F[Shelter Service<br/>Port: 8092/8094]
        F1[Shelter Controller]
        F2[Shelter Service]
        F3[Feign Client]
        F4[Circuit Breaker]
    end

    %% Database Layer
    subgraph "Database Layer"
        G1[(MySQL<br/>user_management)]
        G2[(MySQL<br/>animal)]
        G3[(MySQL<br/>shelter)]
    end

    %% Security Flow
    subgraph "JWT Security Flow"
        H1[Login Request]
        H2[Generate Access Token<br/>15 minutes]
        H3[Generate Refresh Token<br/>7 days]
        H4[HttpOnly Cookie]
        H5[Token Validation]
        H6[Role-Based Access]
    end

    %% Connections
    A --> B
    A1 --> A2
    A2 --> A4
    A3 --> A1
    
    B --> C
    B --> D
    B --> E
    B --> F
    
    B1 --> B2
    B2 --> B3
    B3 --> B4
    
    D --> D1
    D1 --> D2
    D1 --> D3
    D2 --> D5
    D3 --> D6
    D4 --> D1
    
    E --> E1
    E1 --> E2
    E2 --> E3
    
    F --> F1
    F1 --> F2
    F2 --> F3
    F3 --> E
    F2 --> F4
    
    D --> G1
    E --> G2
    F --> G3
    
    %% Security Flow Connections
    H1 --> H2
    H2 --> H3
    H3 --> H4
    H4 --> H5
    H5 --> H6
    
    %% Styling
    classDef frontend fill:#e1f5fe
    classDef gateway fill:#f3e5f5
    classDef microservice fill:#e8f5e8
    classDef database fill:#fff3e0
    classDef security fill:#ffebee
    
    class A,A1,A2,A3,A4 frontend
    class B,B1,B2,B3,B4 gateway
    class D,E,F,D1,D2,D3,D4,D5,D6,E1,E2,E3,F1,F2,F3,F4 microservice
    class G1,G2,G3 database
    class H1,H2,H3,H4,H5,H6 security
```

## 🔐 Enhanced Security Flow Diagram

```mermaid
sequenceDiagram
    participant U as User/Angular
    participant AG as API Gateway
    participant UM as User Management
    participant DB as Database
    participant AM as Animal Service
    participant SM as Shelter Service

    Note over U,SM: Enhanced JWT Security Flow

    %% Login Flow
    U->>AG: POST /auth/login
    AG->>UM: Forward login request
    UM->>DB: Validate credentials
    DB-->>UM: User data
    UM->>UM: Generate Access Token (15min)
    UM->>UM: Generate Refresh Token (7 days)
    UM->>DB: Store refresh token
    UM-->>AG: Access token + HttpOnly cookie
    AG-->>U: Access token + HttpOnly cookie

    %% API Request Flow
    U->>AG: API Request + Access Token
    AG->>AG: Validate JWT token
    AG->>AM: Forward request
    AM-->>AG: Response
    AG-->>U: Response
    
    %% Token Refresh Flow (when needed)
    Note over U,UM: If token expires, automatic refresh
    U->>AG: API Request (expired token)
    AG->>UM: POST /auth/refresh
    UM->>DB: Validate refresh token
    DB-->>UM: Token valid
    UM->>UM: Generate new access token
    UM-->>AG: New access token
    AG->>AM: Retry with new token
    AM-->>AG: Response
    AG-->>U: Response

    %% Logout Flow
    U->>AG: POST /auth/logout
    AG->>UM: Forward logout request
    UM->>DB: Revoke refresh token
    UM->>UM: Clear HttpOnly cookie
    UM-->>AG: Logout success
    AG-->>U: Logout success
```

## 🏛️ Microservices Architecture Overview

```mermaid
graph LR
    subgraph "Client Layer"
        C1[Web Browser]
        C2[Mobile App]
        C3[External API]
    end

    subgraph "Load Balancer (for multiple API Gateway's/microservices)"
        LB[Load Balancer]
    end

    subgraph "API Gateway Layer"
        AG[Spring Cloud Gateway<br/>Security + Routing]
    end

    subgraph "Service Discovery"
        EU[Eureka Server]
    end

    subgraph "Microservices"
        UM[User Management<br/>JWT + Auth]
        AM[Animal Service<br/>CRUD Operations]
        SM[Shelter Service<br/>Business Logic]
    end

    subgraph "Data Layer"
        DB1[(User DB)]
        DB2[(Animal DB)]
        DB3[(Shelter DB)]
    end

    subgraph "Security Layer"
        JWT[JWT Tokens]
        RT[Refresh Tokens]
        RBAC[Role-Based Access]
    end

    C1 --> LB
    C2 --> LB
    C3 --> LB
    LB --> AG
    AG --> EU
    AG --> UM
    AG --> AM
    AG --> SM
    UM --> DB1
    AM --> DB2
    SM --> DB3
    UM --> JWT
    JWT --> RT
    RT --> RBAC
```

## 🔄 Token Management Flow

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> Authenticated: Valid Credentials
    Login --> [*]: Invalid Credentials
    
    Authenticated --> AccessToken: Generate Access Token (15min)
    Authenticated --> RefreshToken: Generate Refresh Token (7 days)
    
    AccessToken --> APIRequest: Make API Calls
    APIRequest --> TokenValid: Token Valid
    APIRequest --> TokenExpired: Token Expired
    
    TokenValid --> APIRequest: Continue
    TokenExpired --> RefreshAccess: Use Refresh Token
    
    RefreshAccess --> NewAccessToken: Refresh Success
    RefreshAccess --> Logout: Refresh Failed
    
    NewAccessToken --> APIRequest: Continue with New Token
    
    APIRequest --> Logout: User Logout
    Logout --> RevokeTokens: Revoke All Tokens
    RevokeTokens --> [*]: Complete Logout
```

## 📊 Component Relationships

```mermaid
erDiagram
    USER ||--o{ REFRESH_TOKEN : has
    USER ||--o{ USER_ROLE : has
    REFRESH_TOKEN ||--|| DEVICE_INFO : stored_on
    
    USER {
        string userId PK
        string email
        string password
        string firstName
        string lastName
        string role
        boolean active
        boolean notLocked
    }
    
    REFRESH_TOKEN {
        long id PK
        string token
        string userId FK
        datetime expiryDate
        datetime createdDate
        boolean isRevoked
        string deviceInfo
        string ipAddress
    }
    
    USER_ROLE {
        string roleName PK
        string description
        string[] permissions
    }
    
    DEVICE_INFO {
        string userAgent
        string ipAddress
        string browser
        string os
    }
```

## 🛡️ Security Architecture

```mermaid
graph TB
    subgraph "Security Layers"
        L1[Frontend Security<br/>HttpOnly Cookies<br/>XSS Protection]
        L2[API Gateway Security<br/>JWT Validation<br/>CORS Configuration]
        L3[Service Security<br/>Role-Based Access<br/>Token Validation]
        L4[Database Security<br/>Encrypted Passwords<br/>Token Blacklisting]
    end
    
    subgraph "Security Components"
        SC1[Authentication Manager]
        SC2[JWT Token Provider]
        SC3[Token Refresh Service]
        SC4[Security Filters]
        SC5[Password Encoder]
    end
    
    subgraph "Security Features"
        SF1[Access Tokens - 15min]
        SF2[Refresh Tokens - 7 days]
        SF3[HttpOnly Cookies]
        SF4[Role-Based Access Control]
        SF5[Device Tracking]
        SF6[IP Address Logging]
        SF7[Token Blacklisting]
    end
    
    L1 --> L2
    L2 --> L3
    L3 --> L4
    
    SC1 --> SC2
    SC2 --> SC3
    SC3 --> SC4
    SC4 --> SC5
    
    SF1 --> SF2
    SF2 --> SF3
    SF3 --> SF4
    SF4 --> SF5
    SF5 --> SF6
    SF6 --> SF7
```

## 🎯 Key Architecture Points

### **Security Flow:**
1. **User Login** → Dual token generation
2. **API Requests** → Access token validation
3. **Token Expiry** → Automatic refresh
4. **Logout** → Token revocation

### **Microservices Communication:**
1. **API Gateway** → Central entry point
2. **Service Discovery** → Eureka registration
3. **Inter-service** → Feign client communication
4. **Database** → Separate databases per service

### **Security Features:**
1. **HttpOnly Cookies** → XSS protection
2. **Token Blacklisting** → Secure logout
3. **Device Tracking** → Security monitoring
4. **Role-Based Access** → Granular permissions

---

