package com.autosalon.backend.auth.service;

import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private AuthAccountRepository accountRepo;
    private UserDetailsServiceImpl uds;

    @BeforeEach
    void setUp() {
        accountRepo = mock(AuthAccountRepository.class);
        uds = new UserDetailsServiceImpl(accountRepo);
    }

    @Test
    void whenUserExistsThenReturnUserDetails() {
        Account acc = new Account();
        acc.setLogin("mia");
        acc.setPassword("pwd");
        Role clientRole = new Role();
        clientRole.setName(ERole.ROLE_CLIENT);
        acc.setRoles(Set.of(clientRole));

        when(accountRepo.findByLogin("mia")).thenReturn(Optional.of(acc));

        var ud = uds.loadUserByUsername("mia");
        assertNotNull(ud);
        assertEquals("mia", ud.getUsername());
        assertEquals("pwd", ud.getPassword());
        assertTrue(
                ud.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))
        );
    }

    @Test
    void whenUserNotFoundThenThrowException() {
        when(accountRepo.findByLogin("ghost")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> uds.loadUserByUsername("ghost")
        );

        assertTrue(
                ex.getMessage().toLowerCase().contains("ghost"),
                "Ожидалось, что сообщение исключения будет содержать сам логин"
        );
    }
}
