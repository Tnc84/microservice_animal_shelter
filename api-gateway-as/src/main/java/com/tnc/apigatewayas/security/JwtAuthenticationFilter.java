package com.tnc.apigatewayas.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Single authentication + authorization filter for the API Gateway.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Allow public endpoints without a token.</li>
 *   <li>Validate the user JWT (HMAC) and reject 401 on missing/invalid/expired token.</li>
 *   <li>Enforce path-based role rules (authorization) and reject 403 on insufficient role.</li>
 *   <li>Forward user context (X-User-Name, X-User-Id, X-User-Authorities) to downstream services.</li>
 * </ul>
 *
 * <p>This filter replaces the previous Spring Security reactive configuration so there is
 * a single source of truth for which endpoints are public, which require auth, and which
 * roles are needed per path.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    public static final int ORDER = -5;

    private final JwtService jwtService;

    /**
     * Endpoints that do not require any authentication.
     */
    private static final List<Predicate<String>> PUBLIC_PATHS = List.of(
            path -> path.startsWith("/actuator/"),
            path -> path.startsWith("/swagger-ui"),
            path -> path.startsWith("/v3/api-docs"),
            path -> path.startsWith("/user-management/auth/"),
            path -> path.startsWith("/auth/"),
            // Happy Tails: list of animals is browsable by anonymous visitors
            path -> path.equals("/animal-microservice/animals/getAll")
    );

    /**
     * Path → allowed roles (ROLE_ prefix required).
     * First match wins; ordering matters (more specific paths first).
     */
    private static final List<RoleRule> ROLE_RULES = List.of(
            new RoleRule(path -> path.startsWith("/user-management/"), Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SHELTER_MANAGER", "ROLE_VET")),
            new RoleRule(path -> path.startsWith("/shelter-microservice/"), Set.of("ROLE_ADMIN", "ROLE_SHELTER_MANAGER")),
            new RoleRule(path -> path.startsWith("/animal-microservice/"), Set.of("ROLE_ADMIN", "ROLE_SHELTER_MANAGER", "ROLE_VET")),
            new RoleRule(path -> path.startsWith("/pet-hotel-microservice/"), Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SHELTER_MANAGER"))
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (isPublicEndpoint(path)) {
            // Public path: allow anonymous access, but if a bearer token is present
            // and valid, still forward user context for downstream method security.
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return chain.filter(exchange);
            }
            String publicToken = authHeader.substring(7).trim();
            if (publicToken.isEmpty() || !jwtService.validateJwtToken(publicToken)) {
                return chain.filter(exchange);
            }
            try {
                String username = jwtService.getUsernameFromJwtToken(publicToken);
                String authorities = jwtService.getAuthoritiesFromJwtToken(publicToken);
                String userId = jwtService.getUserIdFromJwtToken(publicToken);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Authorities", authorities == null ? "" : authorities)
                        .header("X-User-Id", userId == null ? "" : userId)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                log.debug("Ignoring invalid bearer on public path '{}': {}", path, e.getMessage());
                return chain.filter(exchange);
            }
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Empty token provided");
        }

        if (!jwtService.validateJwtToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String username;
        String authorities;
        String userId;
        try {
            username = jwtService.getUsernameFromJwtToken(token);
            authorities = jwtService.getAuthoritiesFromJwtToken(token);
            userId = jwtService.getUserIdFromJwtToken(token);
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            return writeJsonError(exchange, HttpStatus.UNAUTHORIZED, "Error processing token");
        }

        if (!hasRequiredRole(path, authorities)) {
            log.warn("Forbidden for user='{}' on path='{}' (authorities={})", username, path, authorities);
            return writeJsonError(exchange, HttpStatus.FORBIDDEN, "Insufficient role for this resource");
        }

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Name", username)
                .header("X-User-Authorities", authorities == null ? "" : authorities)
                .header("X-User-Id", userId == null ? "" : userId)
                .build();

        log.debug("Authorized user='{}' path='{}' authorities='{}'", username, path, authorities);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream().anyMatch(p -> p.test(path));
    }

    private boolean hasRequiredRole(String path, String authoritiesCsv) {
        RoleRule rule = ROLE_RULES.stream()
                .filter(r -> r.matches.test(path))
                .findFirst()
                .orElse(null);

        // Authenticated but no explicit rule → allow (already validated as logged-in user).
        if (rule == null) {
            return true;
        }
        if (authoritiesCsv == null || authoritiesCsv.isBlank()) {
            return false;
        }

        Set<String> userAuthorities = Arrays.stream(authoritiesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        return userAuthorities.stream().anyMatch(rule.allowedRoles::contains);
    }

    private Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\":\"%s\",\"message\":\"%s\"}", status.getReasonPhrase(), message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    private record RoleRule(Predicate<String> matches, Set<String> allowedRoles) {}
}
