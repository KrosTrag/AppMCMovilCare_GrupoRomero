package com.mcmovilcare.backend.service;

import com.mcmovilcare.backend.api.TicketDtos.AdminLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminAuthService {
    private final String adminEmail;
    private final String adminPassword;
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();

    public AdminAuthService(@Value("${app.admin.email}") String adminEmail,
                            @Value("${app.admin.password}") String adminPassword) {
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    public AdminLoginResponse iniciarSesion(String email, String password) {
        if (!constanteIgual(adminEmail, email) || !constanteIgual(adminPassword, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales de administrador incorrectas");
        }
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        return new AdminLoginResponse(token, adminEmail);
    }

    public void validarToken(String token) {
        if (token == null || !tokens.contains(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión de administrador no válida");
        }
    }

    public void cerrarSesion(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }

    private static boolean constanteIgual(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
}