package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AdminCreateUserRequestTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        AdminCreateUserRequest dto1 = new AdminCreateUserRequest();
        dto1.setLogin("adminUser");
        dto1.setPassword("adminPass");
        dto1.setConfirmPassword("adminPass");
        dto1.setPhoneNumber("+70000000000");
        dto1.setRoles(List.of("ADMIN", "CLIENT"));

        assertEquals("adminUser", dto1.getLogin());
        assertEquals("adminPass", dto1.getPassword());
        assertEquals("adminPass", dto1.getConfirmPassword());
        assertEquals("+70000000000", dto1.getPhoneNumber());
        assertTrue(dto1.getRoles().containsAll(List.of("ADMIN", "CLIENT")));

        AdminCreateUserRequest dto2 = new AdminCreateUserRequest(
                "test",
                "pwd",
                "pwd",
                "+70001112233",
                List.of("CLIENT")
        );
        assertEquals("test", dto2.getLogin());
        assertEquals("pwd", dto2.getPassword());
        assertEquals("pwd", dto2.getConfirmPassword());
        assertEquals("+70001112233", dto2.getPhoneNumber());
        assertEquals(1, dto2.getRoles().size());
        assertEquals("CLIENT", dto2.getRoles().get(0));
    }
}
