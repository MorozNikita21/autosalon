package com.autosalon.backend.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import com.autosalon.backend.auth.dto.RegisterRequest;
import com.autosalon.backend.auth.repository.AuthClientRepository;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthServiceTest {

    @Mock
    private AuthClientRepository authClientRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateClientProfileSaveCorrectClient() {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("testLogin");
        dto.setName("Ivan Petrov");
        dto.setEmail("ivan.petrov@test.com");
        dto.setBirthday("1995-12-31");
        dto.setAddress("Lenina 1");
        dto.setPassport("1234 567890");
        dto.setDriverLicense("AB1234567");
        dto.setFirstLicenseDate("2015-06-20");

        Account account = new Account();
        account.setLogin("testLogin");

        authService.createClientProfile(account, dto);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(authClientRepository, times(1)).save(captor.capture());

        Client saved = captor.getValue();
        assertEquals("testLogin", saved.getLogin());
        assertEquals("Ivan Petrov", saved.getName());
        assertEquals("ivan.petrov@test.com", saved.getEmail());
        assertEquals(LocalDate.parse("1995-12-31"), saved.getBirthday());
        assertEquals("Lenina 1", saved.getAddress());
        assertEquals("1234 567890", saved.getPassport());
        assertEquals("AB1234567", saved.getDriverLicense());
        assertEquals(LocalDate.parse("2015-06-20"), saved.getFirstLicenseDate());
    }
}
