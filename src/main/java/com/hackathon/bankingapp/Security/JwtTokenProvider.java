package com.hackathon.bankingapp.Security;

import com.hackathon.bankingapp.Config.JwtConfig;
import com.hackathon.bankingapp.Exceptions.JwtTokenExpiredException;
import com.hackathon.bankingapp.Exceptions.JwtTokenInvalidException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public String generateToken(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.debug("Generating token for username: {}", username);

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                    .compact();

            log.debug("Token generated successfully");
            return token;
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage(), e);
            throw new JwtTokenInvalidException();
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            log.debug("Username extracted from token: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.error("Token expired");
            throw new JwtTokenExpiredException();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage(), e);
            throw new JwtTokenInvalidException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token);
            log.debug("Token validated successfully");
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage(), e);
            throw new JwtTokenInvalidException();
        } catch (ExpiredJwtException e) {
            log.error("Token expired");
            throw new JwtTokenExpiredException();
        }
    }
}