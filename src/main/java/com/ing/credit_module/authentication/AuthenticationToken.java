package com.ing.credit_module.authentication;

import com.ing.credit_module.exception.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class AuthenticationToken {

    private final Claims claims;

    public AuthenticationToken(String token, String accessTokenSecretKey) throws TokenValidationException {
        try {
            SecretKey key = Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes());

            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenValidationException("Expired or invalid JWT token");
        }
    }

    public Claims getClaims() {
        return claims;
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public String getUsername() {
        return claims.getSubject();
    }
}