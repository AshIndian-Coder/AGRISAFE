package com.agrichain.security;

import com.agrichain.common.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT token service for authentication
 */
@Service
@Slf4j
public class JwtService {

    private final SecretKey secretKey;
    private final String issuer;
    private final String audience;
    private final Duration accessTokenExpiry;
    private final Duration refreshTokenExpiry;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.audience}") String audience,
            @Value("${app.jwt.access-token-expiry}") Duration accessTokenExpiry,
            @Value("${app.jwt.refresh-token-expiry}") Duration refreshTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public TokenPair generateTokenPair(UUID userId, UUID sessionId, UserRole role, UUID organizationId) {
        Instant now = Instant.now();
        
        String accessToken = generateAccessToken(userId, sessionId, role, organizationId, now);
        String refreshToken = generateRefreshToken(userId, sessionId, now);
        
        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresAt(now.plus(accessTokenExpiry))
                .refreshTokenExpiresAt(now.plus(refreshTokenExpiry))
                .sessionId(sessionId)
                .build();
    }

    private String generateAccessToken(UUID userId, UUID sessionId, UserRole role, UUID organizationId, Instant now) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiry)))
                .claim("sid", sessionId.toString())
                .claim("role", role.name())
                .claim("org", organizationId != null ? organizationId.toString() : null)
                .claim("type", "access")
                .signWith(secretKey)
                .compact();
    }

    private String generateRefreshToken(UUID userId, UUID sessionId, Instant now) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshTokenExpiry)))
                .claim("sid", sessionId.toString())
                .claim("type", "refresh")
                .id(UUID.randomUUID().toString())
                .signWith(secretKey)
                .compact();
    }

    public TokenClaims validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                throw new JwtException("Invalid token type");
            }

            return TokenClaims.builder()
                    .userId(UUID.fromString(claims.getSubject()))
                    .sessionId(UUID.fromString(claims.get("sid", String.class)))
                    .role(UserRole.valueOf(claims.get("role", String.class)))
                    .organizationId(parseUUID(claims.get("org", String.class)))
                    .expiresAt(claims.getExpiration().toInstant())
                    .issuedAt(claims.getIssuedAt().toInstant())
                    .build();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Access token expired");
        } catch (JwtException e) {
            log.warn("Invalid access token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid access token");
        }
    }

    public RefreshTokenClaims validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                throw new JwtException("Invalid token type");
            }

            return RefreshTokenClaims.builder()
                    .userId(UUID.fromString(claims.getSubject()))
                    .sessionId(UUID.fromString(claims.get("sid", String.class)))
                    .tokenId(claims.getId())
                    .expiresAt(claims.getExpiration().toInstant())
                    .issuedAt(claims.getIssuedAt().toInstant())
                    .build();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Refresh token expired");
        } catch (JwtException e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        }
    }

    private UUID parseUUID(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Inner classes for token data
    @lombok.Builder
    @lombok.Getter
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
        private Instant accessTokenExpiresAt;
        private Instant refreshTokenExpiresAt;
        private UUID sessionId;
    }

    @lombok.Builder
    @lombok.Getter
    public static class TokenClaims {
        private UUID userId;
        private UUID sessionId;
        private UserRole role;
        private UUID organizationId;
        private Instant expiresAt;
        private Instant issuedAt;
    }

    @lombok.Builder
    @lombok.Getter
    public static class RefreshTokenClaims {
        private UUID userId;
        private UUID sessionId;
        private String tokenId;
        private Instant expiresAt;
        private Instant issuedAt;
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}
