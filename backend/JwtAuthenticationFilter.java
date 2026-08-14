package com.agrichain.security;

import com.agrichain.identity.entity.Session;
import com.agrichain.identity.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * JWT Authentication Filter - validates tokens on each request
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final JwtService.TokenClaims claims = jwtService.validateAccessToken(token);

            // Verify session is still valid
            Optional<Session> sessionOpt = sessionRepository.findValidSession(
                    claims.getSessionId(), 
                    Instant.now()
            );

            if (sessionOpt.isEmpty()) {
                log.warn("Session not found or revoked for user: {}", claims.getUserId());
                filterChain.doFilter(request, response);
                return;
            }

            // Create authentication principal
            AuthenticatedUser principal = AuthenticatedUser.builder()
                    .userId(claims.getUserId())
                    .sessionId(claims.getSessionId())
                    .organizationId(claims.getOrganizationId())
                    .role(claims.getRole())
                    .build();

            // Set authentication in security context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.getRole().name()))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtService.TokenExpiredException e) {
            log.debug("Token expired: {}", e.getMessage());
        } catch (JwtService.InvalidTokenException e) {
            log.debug("Invalid token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Authentication error: ", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip authentication for public endpoints
        return path.startsWith("/v1/auth/") || 
               path.startsWith("/actuator/") ||
               path.equals("/v1/products") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
