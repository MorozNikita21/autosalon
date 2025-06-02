package com.autosalon.backend.auth.security;

import java.util.Date;

import com.autosalon.backend.auth.service.UserDetailsImpl;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        String longSecret = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", longSecret);

        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000);
    }

    @Test
    void testGenerateAndParseToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "testuser",
                "dummyPasswordHash",
                java.util.Collections.emptyList()
        );
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String token = jwtUtils.generateJwtToken(auth);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertTrue(jwtUtils.validateJwtToken(token));

        assertEquals("testuser", jwtUtils.getLoginFromJwtToken(token));
    }

    @Test
    void testValidateExpiredToken() {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000);

        String longSecret = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        byte[] keyBytes = longSecret.getBytes();
        javax.crypto.SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .setSubject("expiredUser")
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void testValidateMalformedToken() {
        // Передаём явно неверную строку
        String badToken = "not.a.jwt.token";
        assertFalse(jwtUtils.validateJwtToken(badToken));

        String longSecret = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        javax.crypto.SecretKey key = Keys.hmacShaKeyFor(longSecret.getBytes());

        String validPart = Jwts.builder()
                .setSubject("userX")
                .setIssuedAt(new Date())
                // expiration через минуту, чтобы он был валидным
                .setExpiration(new Date(new Date().getTime() + 60000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String almostToken = validPart + "abcd";

        assertFalse(jwtUtils.validateJwtToken(almostToken));
    }
}
