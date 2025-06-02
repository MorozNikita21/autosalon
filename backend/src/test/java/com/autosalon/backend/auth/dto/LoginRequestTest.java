package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        LoginRequest dto1 = new LoginRequest();
        dto1.setLogin("user123");
        dto1.setPassword("pwd!@#");
        assertEquals("user123", dto1.getLogin());
        assertEquals("pwd!@#", dto1.getPassword());

        LoginRequest dto2 = new LoginRequest("abc", "xyz");
        assertEquals("abc", dto2.getLogin());
        assertEquals("xyz", dto2.getPassword());
    }
}
