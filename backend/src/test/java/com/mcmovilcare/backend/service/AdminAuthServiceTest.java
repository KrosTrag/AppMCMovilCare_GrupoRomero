package com.mcmovilcare.backend.service;

import com.mcmovilcare.backend.api.TicketDtos.AdminLoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AdminAuthServiceTest {
    private MockRestServiceServer server;
    private AdminAuthService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new AdminAuthService(builder, "http://auth-service");
    }

    @Test
    void reenviaInicioDeSesionAlServicioDeAutenticacion() {
        server.expect(requestTo("http://auth-service/api/auth/admin"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"token\":\"token-1\",\"email\":\"admin@example.com\"}",
                        org.springframework.http.MediaType.APPLICATION_JSON));

        AdminLoginResponse response = service.iniciarSesion("admin@example.com", "secret");

        assertEquals("token-1", response.token());
        assertEquals("admin@example.com", response.email());
        server.verify();
    }

    @Test
    void validaTokenRemotamenteYRechazaTokenAusente() {
        server.expect(requestTo("http://auth-service/internal/sessions/validate"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-1"))
                .andRespond(withNoContent());

        service.validarToken("token-1");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.validarToken(null));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        server.verify();
    }

    @Test
    void reenviaCierreDeSesionYOmiteTokenVacio() {
        server.expect(requestTo("http://auth-service/api/auth/admin/logout"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-1"))
                .andRespond(withNoContent());

        service.cerrarSesion("token-1");
        service.cerrarSesion(" ");

        server.verify();
    }

    @Test
    void traduceErroresDelServicioDeAutenticacion() {
        server.expect(requestTo("http://auth-service/api/auth/admin"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        server.expect(requestTo("http://auth-service/internal/sessions/validate"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseStatusException unauthorized = assertThrows(ResponseStatusException.class,
                () -> service.iniciarSesion("admin@example.com", "incorrecta"));
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatusCode());

        ResponseStatusException unavailable = assertThrows(ResponseStatusException.class,
                () -> service.validarToken("token-1"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, unavailable.getStatusCode());
        server.verify();
    }

    @Test
    void traduceErroresDeConexionComoServicioNoDisponible() {
        RestClient.Builder offlineBuilder = RestClient.builder()
                .requestFactory((uri, method) -> {
                    throw new IOException("Servicio desconectado");
                });
        AdminAuthService offlineService = new AdminAuthService(offlineBuilder, "http://auth-service");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> offlineService.validarToken("token-1"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }
}