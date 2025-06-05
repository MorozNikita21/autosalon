package com.autosalon.backend.auth.controller;

import com.autosalon.backend.auth.dto.AdminCreateUserRequest;
import com.autosalon.backend.auth.dto.AdminUpdateRolesRequest;
import com.autosalon.backend.auth.dto.MessageResponse;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthAccountRepository authAccountRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder   encoder;

    public AdminController(AuthAccountRepository authAccountRepository,
                           AuthRoleRepository authRoleRepository,
                           PasswordEncoder encoder) {
        this.authAccountRepository = authAccountRepository;
        this.authRoleRepository    = authRoleRepository;
        this.encoder           = encoder;
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUserByAdmin(@Valid @RequestBody AdminCreateUserRequest req) {
        if (authAccountRepository.existsByLogin(req.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: Логин уже занят."));
        }

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: пароль и подтверждение пароля не совпадают."));
        }

        Account account = new Account();
        account.setLogin(req.getLogin());
        account.setPassword(encoder.encode(req.getPassword()));
        account.setPhoneNumber(req.getPhoneNumber());
        authAccountRepository.save(account);

        Set<Role> rolesToAssign = new HashSet<>();
        for (String roleNameStr : req.getRoles()) {
            ERole eRole;
            try {
                eRole = ERole.valueOf(roleNameStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Ошибка: Роль '" + roleNameStr + "' не существует."));
            }

            Role role = authRoleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Ошибка: Роль " + eRole + " не найдена."));
            rolesToAssign.add(role);
        }

        account.setRoles(rolesToAssign);
        authAccountRepository.save(account);

        return ResponseEntity.ok(new MessageResponse("Пользователь успешно создан с ролями: " + req.getRoles()));
    }

    @PutMapping("/update-roles")
    public ResponseEntity<?> updateRolesByAdmin(@Valid @RequestBody AdminUpdateRolesRequest req) {
        Account account = authAccountRepository.findByLogin(req.getLogin())
                .orElseThrow(() -> new RuntimeException("Ошибка: Пользователь с логином не найден " + req.getLogin()));

        Set<Role> newRoles = new HashSet<>();
        for (String roleNameStr : req.getRoles()) {
            ERole eRole;
            try {
                eRole = ERole.valueOf(roleNameStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Ошибка: Роль '" + roleNameStr + "' не существует."));
            }
            Role role = authRoleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Ошибка: Роль " + eRole + " не найдена."));
            newRoles.add(role);
        }

        account.setRoles(newRoles);
        authAccountRepository.save(account);

        return ResponseEntity.ok(new MessageResponse("Роли успешно обновлены для пользователя: " + req.getLogin()));
    }

    @DeleteMapping("/delete-user/{login}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable String login) {
        if (!authAccountRepository.existsByLogin(login)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: Пользователь с логином не найден " + login));
        }
        authAccountRepository.deleteById(login);
        return ResponseEntity.ok(new MessageResponse("Пользователь удалён: " + login));
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<Account> users = authAccountRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all-roles")
    public ResponseEntity<?> getAllRoles() {
        List<String> roleNames = authRoleRepository.findAll().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleNames);
    }
}
