package com.hackathon.bankingapp.Security;

import com.hackathon.bankingapp.Config.JwtConfig;
import com.hackathon.bankingapp.Exceptions.JwtTokenExpiredException;
import com.hackathon.bankingapp.Exceptions.JwtTokenInvalidException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public String generateToken(Authentication authentication) {
        try {
            String username = authentication.getName();
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                    .compact();
        } catch (Exception e) {
            throw new JwtTokenInvalidException();
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException();
        } catch (Exception e) {
            throw new JwtTokenInvalidException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new JwtTokenInvalidException();
        } catch (MalformedJwtException e) {
            throw new JwtTokenInvalidException();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new JwtTokenInvalidException();
        } catch (IllegalArgumentException e) {
            throw new JwtTokenInvalidException();
        }
    }
}