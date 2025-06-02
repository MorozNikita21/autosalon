package com.autosalon.backend.auth.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.autosalon.backend.auth.dto.JwtResponse;
import com.autosalon.backend.auth.dto.LoginRequest;
import com.autosalon.backend.auth.dto.MessageResponse;
import com.autosalon.backend.auth.dto.RegisterRequest;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.auth.security.JwtUtils;
import com.autosalon.backend.auth.service.AuthService;
import com.autosalon.backend.auth.service.UserDetailsImpl;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.Client;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthAccountRepository authAccountRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          AuthAccountRepository authAccountRepository,
                          AuthRoleRepository authRoleRepository,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authAccountRepository = authAccountRepository;
        this.authRoleRepository = authRoleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Неверное имя пользователя или пароль."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (authAccountRepository.existsByLogin(signUpRequest.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: Логин уже занят другим пользователем."));
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: пароль и подтверждение пароля не совпадают."));
        }

        Account account = new Account();
        account.setLogin(signUpRequest.getLogin());
        account.setPassword(encoder.encode(signUpRequest.getPassword()));
        account.setPhoneNumber(signUpRequest.getPhoneNumber());

        Role userRole = authRoleRepository.findByName(ERole.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Ошибка: Роль не найдена."));
        account.setRoles(Set.of(userRole));

        authAccountRepository.save(account);

        authService.createClientProfile(account, signUpRequest);

        return ResponseEntity.ok(new MessageResponse("Пользователь успешно зарегистрирован."));
    }
}
