package com.smartpaymentsystem.security;

import com.smartpaymentsystem.config.JwtProperties;
import com.smartpaymentsystem.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtTokenService {
    private final JwtProperties jwtProperties;

    private Key signingKey() {
       return Keys.hmacShaKeyFor(
         jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
       );
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        Date issuedAt = Date.from(now);
        Date expiration = Date.from(
                now.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES)
        );

        var builder = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim("role", user.getRole().name())
                .signWith(signingKey(), SignatureAlgorithm.HS256);

        if (user.getBusiness() != null) {
            builder.claim("businessId", user.getBusiness().getId());
        }
        return builder.compact();
    }

    private Claims parseClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token);

        return jws.getBody();
    }

    public Long extractUserId(String token) {
        String subject = parseClaims(token).getSubject();
        return Long.valueOf(subject);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
