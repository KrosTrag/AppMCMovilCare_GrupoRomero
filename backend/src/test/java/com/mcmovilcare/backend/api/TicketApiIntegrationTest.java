package com.mcmovilcare.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmovilcare.backend.domain.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:care-api-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.admin.email=admin@test.local",
        "app.admin.password=test-password"
})
@AutoConfigureMockMvc
class TicketApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TicketRepository ticketRepository;

    @BeforeEach
    void limpiarSolicitudes() {
        ticketRepository.deleteAll();
    }

    @Test
    void createCustomerEditAndAdminFlow() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteNombre":"Ana","correoCliente":"ana@example.com","equipoModelo":"Laptop","numeroSerie":"SN-7","diagnostico":"No enciende"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("Ingresado"))
                .andReturn();
        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String id = created.get("id").asText();
        assertNotNull(id);

        mockMvc.perform(get("/api/tickets/{id}", id).param("email", "ana@example.com"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/tickets/{id}", id)
                        .param("email", "ana@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnostico\":\"Pantalla rota\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Pantalla rota"));
        mockMvc.perform(put("/api/tickets/{id}", id)
                        .param("email", "otro@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnostico\":\"Cambio no autorizado\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/tickets")).andExpect(status().isUnauthorized());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.local\",\"password\":\"test-password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(get("/api/admin/tickets").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mockMvc.perform(patch("/api/admin/tickets/{id}/status", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"Reparado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Reparado"));
    }
}