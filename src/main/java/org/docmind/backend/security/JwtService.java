package org.docmind.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.docmind.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "role",
                user.getRole().name()
        );
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(
                        new Date(System.currentTimeMillis())
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpiration
                        )
                )
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token) {

        Claims claims =
                extractAllClaims(token);

        return claims.getSubject();
    }

    public Date extractExpiration(String token) {

        Claims claims =
                extractAllClaims(token);

        return claims.getExpiration();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername()
        ) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(
            String token
    ) {

        Date expirationDate =
                extractExpiration(token);

        return expirationDate.before(
                new Date()
        );
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes()
        );
    }
}