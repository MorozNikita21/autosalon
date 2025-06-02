package com.autosalon.backend.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterRequestTest {

    @Test
    void testGetterSetterAndAllArgsConstructor() {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("ivan");
        dto.setPassword("pass123");
        dto.setConfirmPassword("pass123");
        dto.setPhoneNumber("+70001234567");
        dto.setName("Ivan Ivanov");
        dto.setEmail("ivan@mail.com");
        dto.setBirthday("2000-01-01");
        dto.setAddress("Moscow");
        dto.setPassport("1234 567890");
        dto.setDriverLicense("DL123456");
        dto.setFirstLicenseDate("2018-05-10");

        assertEquals("ivan", dto.getLogin());
        assertEquals("pass123", dto.getPassword());
        assertEquals("pass123", dto.getConfirmPassword());
        assertEquals("+70001234567", dto.getPhoneNumber());
        assertEquals("Ivan Ivanov", dto.getName());
        assertEquals("ivan@mail.com", dto.getEmail());
        assertEquals("2000-01-01", dto.getBirthday());
        assertEquals("Moscow", dto.getAddress());
        assertEquals("1234 567890", dto.getPassport());
        assertEquals("DL123456", dto.getDriverLicense());
        assertEquals("2018-05-10", dto.getFirstLicenseDate());

        RegisterRequest dto2 = new RegisterRequest(
                "petr",
                "pwd",
                "pwd",
                "+70007654321",
                "Petr Petrov",
                "petr@mail.com",
                "1995-07-15",
                "SPb",
                "2345 678901",
                "DL987654",
                "2019-07-20"
        );
        assertEquals("petr", dto2.getLogin());
        assertEquals("pwd", dto2.getPassword());
        assertEquals("pwd", dto2.getConfirmPassword());
        assertEquals("+70007654321", dto2.getPhoneNumber());
        assertEquals("Petr Petrov", dto2.getName());
        assertEquals("petr@mail.com", dto2.getEmail());
        assertEquals("1995-07-15", dto2.getBirthday());
        assertEquals("SPb", dto2.getAddress());
        assertEquals("2345 678901", dto2.getPassport());
        assertEquals("DL987654", dto2.getDriverLicense());
        assertEquals("2019-07-20", dto2.getFirstLicenseDate());
    }
}
