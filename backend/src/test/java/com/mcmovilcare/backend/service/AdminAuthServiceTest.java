package com.mcmovilcare.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminAuthServiceTest {
    private final AdminAuthService service = new AdminAuthService("admin@example.com", "secret");

    @Test
    void emiteTokenParaCredencialesDeAdministradorValidas() {
        var response = service.iniciarSesion("admin@example.com", "secret");

        assertNotNull(response.token());
        assertEquals("admin@example.com", response.email());
        service.validarToken(response.token());
        service.cerrarSesion(response.token());
        assertThrows(ResponseStatusException.class, () -> service.validarToken(response.token()));
    }

    @Test
    void rechazaCredencialesIncorrectasYTokenDesconocido() {
        assertThrows(ResponseStatusException.class,
                () -> service.iniciarSesion("admin@example.com", "incorrecta"));
        assertThrows(ResponseStatusException.class, () -> service.validarToken("unknown"));
    }
}