package com.autosalon.backend.auth.controller;

import com.autosalon.backend.auth.dto.LoginRequest;
import com.autosalon.backend.auth.dto.RegisterRequest;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthClientRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.auth.security.JwtUtils;
import com.autosalon.backend.auth.service.AuthService;
import com.autosalon.backend.auth.service.UserDetailsImpl;
import com.autosalon.backend.auth.service.UserDetailsServiceImpl;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthAccountRepository authAccountRepository;

    @MockBean
    private AuthRoleRepository authRoleRepository;

    @MockBean
    private AuthClientRepository authClientRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthService authService;

    private RegisterRequest validRegisterDto;
    private LoginRequest validLoginDto;

    @BeforeEach
    void setUp() {
        validRegisterDto = new RegisterRequest();
        validRegisterDto.setLogin("newClient");
        validRegisterDto.setPassword("Passw0rd!");
        validRegisterDto.setConfirmPassword("Passw0rd!");
        validRegisterDto.setPhoneNumber("+79160001122");
        validRegisterDto.setName("Иван Петров");
        validRegisterDto.setEmail("ivan.petrov@example.com");
        validRegisterDto.setBirthday("1985-10-15");
        validRegisterDto.setAddress("г. Москва, ул. Ленина, д. 10");
        validRegisterDto.setPassport("1234 567890");
        validRegisterDto.setDriverLicense("DL099988");
        validRegisterDto.setFirstLicenseDate("2003-06-20");

        validLoginDto = new LoginRequest();
        validLoginDto.setLogin("loginA");
        validLoginDto.setPassword("correctPassword");
    }

    @Test
    void testRegisterClientSuccess() throws Exception {
        when(authAccountRepository.existsByLogin("newClient")).thenReturn(false);

        Role clientRole = Role.builder()
                .id(1L)
                .name(ERole.CLIENT)
                .build();
        when(authRoleRepository.findByName(ERole.CLIENT))
                .thenReturn(Optional.of(clientRole));

        when(passwordEncoder.encode("Passw0rd!")).thenReturn("encodedPassword");

        when(authAccountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(validRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", Matchers.is("Пользователь успешно зарегистрирован.")));

        verify(authService, times(1))
                .createClientProfile(any(Account.class), any(RegisterRequest.class));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(authAccountRepository, times(1)).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();
        assertEquals("newClient", savedAccount.getLogin());
        assertEquals("encodedPassword", savedAccount.getPassword());
        assertEquals("+79160001122", savedAccount.getPhoneNumber());
        assertEquals(1, savedAccount.getRoles().size());
        assertEquals(ERole.CLIENT, savedAccount.getRoles().iterator().next().getName());
    }

    @Test
    void testRegisterClientLoginTaken() throws Exception {
        when(authAccountRepository.existsByLogin("newClient")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ошибка: Логин уже занят другим пользователем."));

        verify(authAccountRepository, never()).save(any(Account.class));
        verify(authService, never()).createClientProfile(any(Account.class), any());
    }

    @Test
    void testRegisterClientPasswordMismatch() throws Exception {
        RegisterRequest badDto = new RegisterRequest();
        badDto.setLogin("userX");
        badDto.setPassword("abc");
        badDto.setConfirmPassword("xyz");
        badDto.setPhoneNumber("+70000000001");
        badDto.setName("User X");
        badDto.setEmail("userx@test.com");
        badDto.setBirthday("1999-09-09");
        badDto.setAddress("Addr");
        badDto.setPassport("BB 222222");
        badDto.setDriverLicense("DL 333333");
        badDto.setFirstLicenseDate("2011-11-11");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password")
                        .value("size must be between 6 and 40"))
                .andExpect(jsonPath("$.errors.confirmPassword")
                        .value("size must be between 6 and 40"));

        verify(authAccountRepository, never()).save(any(Account.class));
        verify(authService, never()).createClientProfile(any(Account.class), any());
    }

    @Test
    void testAuthenticateUserSuccess() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "loginA",
                "hashPass",
                Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENT")
                )
        );
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);

        when(jwtUtils.generateJwtToken(authToken)).thenReturn("jwt123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt123"))
                .andExpect(jsonPath("$.login").value("loginA"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENT"));
    }

    @Test
    void testAuthenticateUserWrongCredentials() throws Exception {
        LoginRequest badLogin = new LoginRequest();
        badLogin.setLogin("nonexistent");
        badLogin.setPassword("wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Неверное имя пользователя или пароль."));
    }
}
