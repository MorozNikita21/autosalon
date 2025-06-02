package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageResponseTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        MessageResponse dto1 = new MessageResponse();
        dto1.setMessage("Привет!");
        assertEquals("Привет!", dto1.getMessage());

        MessageResponse dto2 = new MessageResponse("Работает");
        assertEquals("Работает", dto2.getMessage());
    }
}
