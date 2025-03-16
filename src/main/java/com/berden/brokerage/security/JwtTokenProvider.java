package com.berden.brokerage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private final String jwtSecretKey;
    private final int jwtExpInMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecretKey, @Value("${jwt.expiration}") int jwtExpInMs) {
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpInMs = jwtExpInMs;
    }

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(principal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpInMs))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token) {
        return Optional.ofNullable(token)
                .filter(t -> !t.trim().isEmpty())
                .flatMap(this::extractClaimsSafely)
                .map(this::isValidClaims)
                .orElse(false);
    }

    private String generateKey() {
        SecretKey secretKey = null;
        try {
            secretKey = KeyGenerator.getInstance("HmacSHA256").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Optional<Claims> extractClaimsSafely(String token) {
        try {
            return Optional.of(extractAllClaims(token));
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private boolean isValidClaims(Claims claims) {
        return claims.getSubject() != null &&
                claims.getExpiration() != null &&
                claims.getIssuedAt() != null &&
                !isTokenExpired(claims);
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
