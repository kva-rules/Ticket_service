package com.cognizant.Ticket_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.List;

/**
 * Reads JWTs minted by auth-service. Token shape:
 *   sub          = email
 *   authUserId   = canonical UUID (string)
 *   userId       = numeric Long (auth-side; may be 0 before user-service profile is created)
 *   roles        = list of "ROLE_X" strings
 */
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parseClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getAuthUserIdFromToken(String token) {
        Object v = parseClaims(token).get("authUserId");
        return v != null ? v.toString() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Object roles = parseClaims(token).get("roles");
        return (roles instanceof List) ? (List<String>) roles : Collections.emptyList();
    }
}
