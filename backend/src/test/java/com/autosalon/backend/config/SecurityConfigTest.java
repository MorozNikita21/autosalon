package com.autosalon.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Протестируем SecurityConfig, проверяя, что публичные URL
 * не возвращают 401 (Unauthorized), даже если контента нет,
 * а остальные требуют аутентификацию.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /swagger-ui.html → не 401 (редирект на /swagger-ui/index.html)")
    void swaggerUiHtmlIsNotSecured() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("GET /v3/api-docs → 200 (OpenAPI JSON) и Content-Type application/json")
    void openApiJsonIsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /webjars/some-path → не 401 (в тестовом контексте, скорее всего 404)")
    void swaggerUiResourcesAreNotSecured() throws Exception {
        mockMvc.perform(get("/webjars/swagger-ui/swagger-ui-bundle.js"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /actuator/prometheus → не 401 (в тестовом контексте, скорее всего 404)")
    void actuatorPrometheusIsNotSecured() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/protected → 401 без аутентификации")
    void anyOtherUrlRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/some-protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/protected → 404 при валидной аутентификации (есть аут, но нет контроллера)")
    @WithMockUser(username = "admin", password = "secret123")
    void anyOtherUrlAccessibleWithValidUser() throws Exception {
        mockMvc.perform(get("/api/some-protected"))
                .andExpect(status().isNotFound());
    }
}
