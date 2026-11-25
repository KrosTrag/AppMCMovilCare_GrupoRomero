package com.mcmovilcare.auth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmovilcare.auth.domain.AdminSession;
import com.mcmovilcare.auth.domain.AdminSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-api-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.admin.email=admin@test.local",
        "app.admin.password=test-password"
})
@AutoConfigureMockMvc
class AdminAuthApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AdminSessionRepository sessionRepository;

    @BeforeEach
    void limpiarSesiones() {
        sessionRepository.deleteAll();
    }

    @Test
    void persisteSesionValidaYPermiteInvalidarla() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ADMIN@test.local\",\"password\":\"test-password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode login = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = login.get("token").asText();

        assertEquals(1, sessionRepository.count());
        mockMvc.perform(post("/internal/sessions/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/admin/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/internal/sessions/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/admin/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void rechazaCredencialesIncorrectasYTokensAusentes() throws Exception {
        mockMvc.perform(post("/api/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.local\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/internal/sessions/validate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminaLasSesionesVencidasAlValidarlas() throws Exception {
        sessionRepository.save(new AdminSession("expired-token", "admin@test.local",
                java.time.Instant.now().minusSeconds(1)));

        mockMvc.perform(post("/internal/sessions/validate")
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());

        assertFalse(sessionRepository.existsById("expired-token"));
    }
}