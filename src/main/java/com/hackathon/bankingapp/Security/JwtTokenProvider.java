package com.hackathon.bankingapp.Security;

import com.hackathon.bankingapp.Exceptions.JwtTokenExpiredException;
import com.hackathon.bankingapp.Exceptions.JwtTokenInvalidException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    public String generateToken(Authentication authentication) {
        try {
            String username = authentication.getName();
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                    .compact();
        } catch (Exception e) {
            log.error("Error generating token", e);
            throw new JwtTokenInvalidException();
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired", e);
            throw new JwtTokenExpiredException();
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new JwtTokenInvalidException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String getTokenFromHeader(String headerValue) {
        if (headerValue != null && headerValue.startsWith(prefix + " ")) {
            return headerValue.substring((prefix + " ").length());
        }
        return null;
    }
}