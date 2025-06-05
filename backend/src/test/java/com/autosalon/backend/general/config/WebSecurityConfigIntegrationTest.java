package com.autosalon.backend.general.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthRoleRepository;
import com.autosalon.backend.auth.security.JwtUtils;
import com.autosalon.backend.auth.service.UserDetailsImpl;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "autosalon.app.jwtSecret=0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF",
        "autosalon.app.jwtExpirationMs=60000"
})
class WebSecurityConfigIntegrationTest {

    private final WebSecurityConfig config = new WebSecurityConfig(null, null, null);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthAccountRepository authAccountRepository;

    @Autowired
    private AuthRoleRepository authRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpDatabase() {
        authAccountRepository.deleteAll();
        authRoleRepository.deleteAll();

        Role clientRole = new Role();
        clientRole.setName(ERole.CLIENT);
        authRoleRepository.save(clientRole);

        Role adminRole = new Role();
        adminRole.setName(ERole.ADMIN);
        authRoleRepository.save(adminRole);

        Account client = new Account();
        client.setLogin("clientLogin");
        client.setPassword(passwordEncoder.encode("password"));
        client.setPhoneNumber("+70000000001");
        client.getRoles().add(clientRole);
        authAccountRepository.save(client);

        Account admin = new Account();
        admin.setLogin("adminLogin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setPhoneNumber("+70000000002");
        admin.getRoles().add(adminRole);
        authAccountRepository.save(admin);
    }

    @Test
    @DisplayName("customOpenAPI() должен возвращать non-null OpenAPI объект")
    void customOpenAPIShouldNotBeNull() {
        OpenAPI openAPI = config.customOpenAPI();
        assertNotNull(openAPI, "Метод customOpenAPI не должен возвращать null");
    }

    @Test
    @DisplayName("В OpenAPI должны содержаться компоненты с SecurityScheme \"BearerAuth\"")
    void componentsShouldContainBearerAuthScheme() {
        OpenAPI openAPI = config.customOpenAPI();
        Components components = openAPI.getComponents();
        assertNotNull(components, "Components не должен быть null");

        var schemes = components.getSecuritySchemes();
        assertNotNull(schemes, "SecuritySchemes не должен быть null");
        assertTrue(schemes.containsKey("BearerAuth"),
                "SecuritySchemes должен содержать ключ \"BearerAuth\"");

        SecurityScheme scheme = schemes.get("BearerAuth");
        assertNotNull(scheme, "SecurityScheme по ключу \"BearerAuth\" не должен быть null");

        assertEquals(SecurityScheme.Type.HTTP, scheme.getType(),
                "Type схемы должен быть HTTP");
        assertEquals("bearer", scheme.getScheme(),
                "Scheme должен быть \"bearer\"");
        assertEquals("JWT", scheme.getBearerFormat(),
                "BearerFormat должен быть \"JWT\"");
    }

    @Test
    @DisplayName("В OpenAPI должен присутствовать SecurityRequirement с ключом \"BearerAuth\"")
    void securityItemsShouldContainBearerAuthRequirement() {
        OpenAPI openAPI = config.customOpenAPI();

        var securityRequirements = openAPI.getSecurity();
        assertNotNull(securityRequirements, "Список SecurityRequirement не должен быть null");
        assertFalse(securityRequirements.isEmpty(), "Список SecurityRequirement не должен быть пустым");

        boolean found = securityRequirements.stream()
                .anyMatch(req -> req.containsKey("BearerAuth"));
        assertTrue(found, "Должен быть SecurityRequirement с ключом \"BearerAuth\"");
    }

    @Test
    void publicAuthEndpointsShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status,
                            "GET /api/auth/login должен быть доступен (не 401), получил " + status
                    );
                });

        mockMvc.perform(get("/api/auth/register"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status,
                            "GET /api/auth/register должен быть доступен (не 401), получил " + status
                    );
                });
    }

    @Test
    void swaggerAndApiDocsShouldBePublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status,
                            "/swagger-ui/index.html должен быть доступен (не 401), получил " + status
                    );
                });

        mockMvc.perform(get("/v3/api-docs/"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertNotEquals(
                            401, status,
                            "/v3/api-docs/ должен быть доступен (не 401), получил " + status
                    );
                });
    }

    @Test
    void protectedEndpointShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/all-users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointShouldReturn403ForNonAdminToken() throws Exception {
        UserDetailsImpl clientUser = new UserDetailsImpl(
                "clientLogin",
                "dummyHash",
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENT")
                )
        );
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        clientUser,
                        null,
                        clientUser.getAuthorities()
                );
        String tokenClient = jwtUtils.generateJwtToken(authToken);

        mockMvc.perform(get("/api/admin/all-users")
                        .header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointShouldReturn200ForAdminToken() throws Exception {
        UserDetailsImpl adminUser = new UserDetailsImpl(
                "adminLogin",
                "dummyHash",
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        adminUser,
                        null,
                        adminUser.getAuthorities()
                );
        String tokenAdmin = jwtUtils.generateJwtToken(authToken);

        mockMvc.perform(get("/api/admin/all-users")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }
}
