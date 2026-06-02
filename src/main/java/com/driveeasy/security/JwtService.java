package com.driveeasy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Handles all JWT operations:
 *  - Generating signed tokens for authenticated users
 *  - Validating incoming tokens (signature + expiry)
 *  - Extracting claims (username, role, expiry) from tokens
 *
 * Uses HMAC-SHA256 (HS256) — symmetric signing, same key signs and verifies.
 * This is correct for a single-server app. For microservices, switch to RS256.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    // ── Token generation ────────────────────────────────────────────────────

    /**
     * Generates a JWT for the given user with role embedded as a custom claim.
     * Role is included so API clients can show/hide UI without a round-trip.
     */
    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        return buildToken(extraClaims, userDetails);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Validation ──────────────────────────────────────────────────────────

    /**
     * Returns true only if the token's subject matches the UserDetails username
     * AND the token has not expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Claims extraction ───────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Key ─────────────────────────────────────────────────────────────────

    /**
     * Derives an HMAC-SHA256 key from the configured secret string.
     * Keys.hmacShaKeyFor requires at least 256 bits — enforced by the
     * minimum secret length documented in application.properties.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}