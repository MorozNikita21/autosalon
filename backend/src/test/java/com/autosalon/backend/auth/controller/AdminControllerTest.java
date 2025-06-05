package com.autosalon.backend.auth.controller;

import com.autosalon.backend.auth.dto.AdminCreateUserRequest;
import com.autosalon.backend.auth.dto.AdminUpdateRolesRequest;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.auth.service.UserDetailsServiceImpl;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import com.autosalon.backend.general.config.WebSecurityConfig;
import com.autosalon.backend.auth.security.AuthEntryPointJwt;
import com.autosalon.backend.auth.security.AuthTokenFilter;
import com.autosalon.backend.auth.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({WebSecurityConfig.class, JwtUtils.class, AuthTokenFilter.class, AuthEntryPointJwt.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthAccountRepository accountRepo;

    @MockBean
    private AuthRoleRepository roleRepo;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role roleClient;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        roleClient = new Role();
        roleClient.setId(2L);
        roleClient.setName(ERole.CLIENT);

        roleAdmin = new Role();
        roleAdmin.setId(1L);
        roleAdmin.setName(ERole.ADMIN);
    }

    @Test
    void whenCreateUserAsAdminAndValidRequestThenReturnMessage() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("newUser");
        req.setPassword("password1");
        req.setConfirmPassword("password1");
        req.setPhoneNumber("+71234567890");
        req.setRoles(List.of("CLIENT", "ADMIN"));

        when(accountRepo.existsByLogin("newUser")).thenReturn(false);
        when(roleRepo.findByName(ERole.CLIENT)).thenReturn(Optional.of(roleClient));
        when(roleRepo.findByName(ERole.ADMIN)).thenReturn(Optional.of(roleAdmin));
        when(accountRepo.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/admin/create-user")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Пользователь успешно создан")));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(2)).save(captor.capture());
        Account savedOnce = captor.getAllValues().get(0);
        Account savedWithRoles = captor.getAllValues().get(1);

        assertEquals("newUser", savedOnce.getLogin());
        assertNotNull(savedOnce.getPassword());
        assertTrue(savedOnce.getPhoneNumber().equals("+71234567890"));

        Set<Role> assignedRoles = savedWithRoles.getRoles();
        assertThat(assignedRoles, hasSize(2));
        assertTrue(assignedRoles.stream().anyMatch(r -> r.getName() == ERole.CLIENT));
        assertTrue(assignedRoles.stream().anyMatch(r -> r.getName() == ERole.ADMIN));
    }

    @Test
    void whenCreateUserLoginExistsThenBadRequest() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("existing");
        req.setPassword("password1");
        req.setConfirmPassword("password1");
        req.setPhoneNumber("+71234567890");
        req.setRoles(List.of("CLIENT"));

        when(accountRepo.existsByLogin("existing")).thenReturn(true);

        mockMvc.perform(post("/api/admin/create-user")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ошибка: Логин уже занят.")));

        verify(accountRepo, never()).save(any());
    }

    @Test
    void whenCreateUserPasswordsDontMatchThenBadRequest() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("newUser");
        req.setPassword("password1");
        req.setConfirmPassword("different");
        req.setPhoneNumber("+71234567890");
        req.setRoles(List.of("CLIENT"));

        when(accountRepo.existsByLogin("newUser")).thenReturn(false);

        mockMvc.perform(post("/api/admin/create-user")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message", containsString("Ошибка: пароль и подтверждение пароля не совпадают.")))
                ;

        verify(accountRepo, never()).save(any());
    }

    @Test
    void whenCreateUserRoleNotFoundThenBadRequest() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("another");
        req.setPassword("password1");
        req.setConfirmPassword("password1");
        req.setPhoneNumber("+71234560000");
        req.setRoles(List.of("UNKNOWN_ROLE"));

        when(accountRepo.existsByLogin("another")).thenReturn(false);

        when(accountRepo.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/admin/create-user")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ошибка: Роль 'UNKNOWN_ROLE' не существует.")));

        verify(accountRepo, times(1)).save(any(Account.class));
    }

    @Test
    void whenCreateUserNonAdminThenForbidden() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("someLogin");
        req.setPassword("password1");
        req.setConfirmPassword("password1");
        req.setPhoneNumber("+71230000000");
        req.setRoles(List.of("CLIENT"));

        mockMvc.perform(post("/api/admin/create-user")
                        .with(user("clientUser").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verify(accountRepo, never()).save(any());
    }

    @Test
    void whenCreateUserNoAuthThenUnauthorized() throws Exception {
        AdminCreateUserRequest req = new AdminCreateUserRequest();
        req.setLogin("noAuthUser");
        req.setPassword("password");
        req.setConfirmPassword("password");
        req.setPhoneNumber("+71231112222");
        req.setRoles(List.of("CLIENT"));

        mockMvc.perform(post("/api/admin/create-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verify(accountRepo, never()).save(any());
    }

    @Test
    void whenUpdateRolesAsAdminAndSuccessThenReturnOk() throws Exception {
        AdminUpdateRolesRequest req = new AdminUpdateRolesRequest();
        req.setLogin("john");
        req.setRoles(List.of("ADMIN", "CLIENT"));

        Account existing = new Account();
        existing.setLogin("john");
        existing.setRoles(new HashSet<>());
        when(accountRepo.findByLogin("john")).thenReturn(Optional.of(existing));
        when(roleRepo.findByName(ERole.ADMIN)).thenReturn(Optional.of(roleAdmin));
        when(roleRepo.findByName(ERole.CLIENT)).thenReturn(Optional.of(roleClient));
        when(accountRepo.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/admin/update-roles")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Роли успешно обновлены для пользователя: john")));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(1)).save(captor.capture());
        Account saved = captor.getValue();
        assertEquals("john", saved.getLogin());
        Set<Role> updatedRoles = saved.getRoles();
        assertThat(updatedRoles, hasSize(2));
        assertTrue(updatedRoles.stream().anyMatch(r -> r.getName() == ERole.ADMIN));
        assertTrue(updatedRoles.stream().anyMatch(r -> r.getName() == ERole.CLIENT));
    }

    @Test
    void whenUpdateRolesNonexistentLoginThenThrowServletException() {
        AdminUpdateRolesRequest req = new AdminUpdateRolesRequest();
        req.setLogin("unknown");
        req.setRoles(List.of("CLIENT"));

        when(accountRepo.findByLogin("unknown")).thenReturn(Optional.empty());

        ServletException ex = assertThrows(ServletException.class, () -> {
            mockMvc.perform(put("/api/admin/update-roles")
                            .with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andReturn();
        });

        Throwable cause = ex.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof RuntimeException);
        assertTrue(cause.getMessage().contains("Ошибка: Пользователь с логином не найден unknown"));

        verify(accountRepo, never()).save(any());
    }


    @Test
    void whenUpdateRolesRoleNotFoundThenBadRequest() throws Exception {
        AdminUpdateRolesRequest req = new AdminUpdateRolesRequest();
        req.setLogin("john");
        req.setRoles(List.of("INVALID"));

        Account existing = new Account();
        existing.setLogin("john");
        existing.setRoles(new HashSet<>());
        when(accountRepo.findByLogin("john")).thenReturn(Optional.of(existing));

        mockMvc.perform(put("/api/admin/update-roles")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ошибка: Роль 'INVALID' не существует.")));

        verify(accountRepo, never()).save(any());
    }

    @Test
    void whenUpdateRolesNonAdminThenForbidden() throws Exception {
        AdminUpdateRolesRequest req = new AdminUpdateRolesRequest();
        req.setLogin("john");
        req.setRoles(List.of("CLIENT"));

        mockMvc.perform(put("/api/admin/update-roles")
                        .with(user("clientUser").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verify(accountRepo, never()).findByLogin(anyString());
    }

    @Test
    void whenUpdateRolesNoAuthThenUnauthorized() throws Exception {
        AdminUpdateRolesRequest req = new AdminUpdateRolesRequest();
        req.setLogin("john");
        req.setRoles(List.of("CLIENT"));

        mockMvc.perform(put("/api/admin/update-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verify(accountRepo, never()).findByLogin(anyString());
    }

    @Test
    void whenDeleteUserAsAdminAndExistsThenReturnOk() throws Exception {
        String login = "toDelete";
        when(accountRepo.existsByLogin(login)).thenReturn(true);
        doNothing().when(accountRepo).deleteById(login);

        mockMvc.perform(delete("/api/admin/delete-user/{login}", login)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Пользователь удалён: " + login)));

        verify(accountRepo, times(1)).deleteById(login);
    }

    @Test
    void whenDeleteUserNonexistentLoginThenBadRequest() throws Exception {
        String login = "noSuchUser";
        when(accountRepo.existsByLogin(login)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/delete-user/{login}", login)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Ошибка: Пользователь с логином не найден " + login)));

        verify(accountRepo, never()).deleteById(anyString());
    }

    @Test
    void whenDeleteUserNonAdminThenForbidden() throws Exception {
        mockMvc.perform(delete("/api/admin/delete-user/{login}", "userX")
                        .with(user("clientUser").roles("CLIENT")))
                .andExpect(status().isForbidden());

        verify(accountRepo, never()).existsByLogin(anyString());
    }

    @Test
    void whenDeleteUserNoAuthThenUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/admin/delete-user/{login}", "userX"))
                .andExpect(status().isUnauthorized());

        verify(accountRepo, never()).existsByLogin(anyString());
    }

    @Test
    void whenGetAllUsersAsAdminThenReturnUsers() throws Exception {
        Account acc1 = new Account();
        acc1.setLogin("user1");
        Account acc2 = new Account();
        acc2.setLogin("user2");

        when(accountRepo.findAll()).thenReturn(List.of(acc1, acc2));

        mockMvc.perform(get("/api/admin/all-users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$[0].login", Matchers.is("user1")))
                .andExpect(jsonPath("$[1].login", Matchers.is("user2")));

        verify(accountRepo, times(1)).findAll();
    }

    @Test
    void whenGetAllUsersAsClientThenForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/all-users")
                        .with(user("clientUser").roles("CLIENT")))
                .andExpect(status().isForbidden());

        verify(accountRepo, never()).findAll();
    }

    @Test
    void whenGetAllUsersNoAuthThenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/all-users"))
                .andExpect(status().isUnauthorized());

        verify(accountRepo, never()).findAll();
    }

    @Test
    void whenGetAllUsersEmptyListThenReturnEmptyArray() throws Exception {
        when(accountRepo.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/all-users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(0)));

        verify(accountRepo, times(1)).findAll();
    }

    @Test
    void whenGetAllRolesAsAdminThenReturnRoleNames() throws Exception {
        when(roleRepo.findAll()).thenReturn(List.of(roleAdmin, roleClient));

        mockMvc.perform(get("/api/admin/all-roles")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$", containsInAnyOrder("ADMIN", "CLIENT")));

        verify(roleRepo, times(1)).findAll();
    }

    @Test
    void whenGetAllRolesEmptyRolesThenReturnEmptyArray() throws Exception {
        when(roleRepo.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/all-roles")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(0)));

        verify(roleRepo, times(1)).findAll();
    }

    @Test
    void whenGetAllRolesAsClientThenForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/all-roles")
                        .with(user("clientUser").roles("CLIENT")))
                .andExpect(status().isForbidden());

        verify(roleRepo, never()).findAll();
    }
}
