package com.berden.brokerage.unit.security;

import com.berden.brokerage.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    private String secretKey = "BaOzZK2Q90gpBa3Jz7Z48gd+Kcp9A0UUnJGMIsBwDO71YcG6X4dSwpBm3L03YScd";
    private int jwtExpInMs = 10000;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secretKey, jwtExpInMs);
    }

    @Test
    void testGenerateToken_ShouldReturnValidToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testExtractUsername_ShouldReturnCorrectUsername() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtTokenProvider.generateToken(authentication);
        String extractedUsername = jwtTokenProvider.extractUsername(token);

        assertEquals("testUser", extractedUsername);
    }

    @Test
    void testValidateToken_ShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_ShouldReturnFalseForExpiredToken() {
        Date pastDate = new Date(System.currentTimeMillis() - jwtExpInMs * 2);

        String expiredToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(pastDate)
                .expiration(new Date(pastDate.getTime() + jwtExpInMs))
                .signWith(getKey())
                .compact();

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
