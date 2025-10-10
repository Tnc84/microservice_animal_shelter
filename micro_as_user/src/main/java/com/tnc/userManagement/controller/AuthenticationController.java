package com.tnc.userManagement.controller;

import com.tnc.userManagement.service.IUserService;
import com.tnc.userManagement.service.constant.SecurityConstant;
import com.tnc.userManagement.service.exception.EmailExistException;
import com.tnc.userManagement.service.model.UserDomain;
import com.tnc.userManagement.service.security.UserPrincipal;
import com.tnc.userManagement.service.security.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller for JWT token generation
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for JWT token generation")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
            )
        );

        // Get user details
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserDomain user = userPrincipal.getUser();

        // Generate JWT token
        String jwtToken = jwtTokenProvider.generateJwtToken(userPrincipal);

        // Return response with token
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, SecurityConstant.TOKEN_PREFIX + jwtToken)
            .body(new LoginResponse(jwtToken, user.getUserId(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register new user and return JWT token")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) 
            throws EmailExistException {
        
        // Create new user
        UserDomain newUser = userService.addNewUserWithSpecificRole(
            registerRequest.firstName(),
            registerRequest.lastName(),
            registerRequest.email(),
            "USER", // Default role
            true,
            true
        );

        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        String jwtToken = jwtTokenProvider.generateJwtToken(userPrincipal);

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.AUTHORIZATION, SecurityConstant.TOKEN_PREFIX + jwtToken)
            .body(new LoginResponse(jwtToken, newUser.getUserId(), newUser.getEmail(), newUser.getRole()));
    }

    // DTOs for request/response using records
    public record LoginRequest(
            String email,
            String password
    ) {}

    public record RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password
    ) {}

    public record LoginResponse(
            String token,
            String userId,
            String email,
            String role
    ) {}
}
