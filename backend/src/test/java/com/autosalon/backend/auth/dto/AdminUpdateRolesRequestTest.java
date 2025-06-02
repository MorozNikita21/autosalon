package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminUpdateRolesRequestTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        AdminUpdateRolesRequest dto1 = new AdminUpdateRolesRequest();
        dto1.setLogin("john");
        dto1.setRoles(List.of("ADMIN", "CLIENT"));

        assertEquals("john", dto1.getLogin());
        assertTrue(dto1.getRoles().containsAll(List.of("ADMIN", "CLIENT")));

        AdminUpdateRolesRequest dto2 = new AdminUpdateRolesRequest("mary", List.of("CLIENT"));
        assertEquals("mary", dto2.getLogin());
        assertEquals(1, dto2.getRoles().size());
        assertEquals("CLIENT", dto2.getRoles().get(0));
    }
}
