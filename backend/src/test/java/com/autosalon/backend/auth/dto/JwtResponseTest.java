package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtResponseTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        JwtResponse dto1 = new JwtResponse();
        dto1.setToken("token123");
        dto1.setLogin("ivan");
        dto1.setRoles(List.of("CLIENT", "ADMIN"));
        assertEquals("token123", dto1.getToken());
        assertEquals("Bearer", dto1.getType());
        assertEquals("ivan", dto1.getLogin());
        assertTrue(dto1.getRoles().contains("CLIENT"));
        assertTrue(dto1.getRoles().contains("ADMIN"));

        JwtResponse dto2 = JwtResponse.builder()
                .token("abc")
                .login("user1")
                .roles(List.of("CLIENT"))
                .build();
        assertEquals("abc", dto2.getToken());
        assertEquals("Bearer", dto2.getType());
        assertEquals("user1", dto2.getLogin());
        assertEquals(1, dto2.getRoles().size());
        assertEquals("CLIENT", dto2.getRoles().get(0));
    }
}
