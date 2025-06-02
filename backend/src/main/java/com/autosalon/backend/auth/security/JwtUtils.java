package com.autosalon.backend.auth.security;

import java.util.Date;

import com.autosalon.backend.auth.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${autosalon.app.jwtSecret}")
    private String jwtSecret;

    @Value("${autosalon.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getLoginFromJwtToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Подпись не совпала (испорчена или неправильная): {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Инвалидный токен: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Токен просрочен: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Неподдерживаемый токен: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Пустые или неверные аргументы: {}", e.getMessage());
        }
        return false;
    }
}
