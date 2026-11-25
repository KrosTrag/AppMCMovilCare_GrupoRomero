package com.mcmovilcare.auth.service;

import com.mcmovilcare.auth.api.AuthDtos.AdminLoginResponse;
import com.mcmovilcare.auth.domain.AdminSession;
import com.mcmovilcare.auth.domain.AdminSessionRepository;
import com.mcmovilcare.auth.domain.AdminUser;
import com.mcmovilcare.auth.domain.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AdminAuthService {
    private final AdminUserRepository userRepository;
    private final AdminSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final long sessionDurationHours;

    public AdminAuthService(AdminUserRepository userRepository,
                            AdminSessionRepository sessionRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.session.expiration-hours}") long sessionDurationHours) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionDurationHours = sessionDurationHours;
    }

    public AdminLoginResponse iniciarSesion(String email, String password) {
        String normalizedEmail = normalizarEmail(email);
        AdminUser admin = userRepository.findById(normalizedEmail)
                .orElseThrow(this::credencialesInvalidas);
        if (!passwordEncoder.matches(password, admin.passwordHash())) {
            throw credencialesInvalidas();
        }
        String token = UUID.randomUUID().toString();
        sessionRepository.save(new AdminSession(token, admin.email(),
                Instant.now().plus(Duration.ofHours(sessionDurationHours))));
        return new AdminLoginResponse(token, admin.email());
    }

    public void validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw sesionInvalida();
        }
        AdminSession session = sessionRepository.findById(token).orElseThrow(this::sesionInvalida);
        if (!session.expiresAt().isAfter(Instant.now())) {
            sessionRepository.deleteById(token);
            throw sesionInvalida();
        }
    }

    public void cerrarSesion(String token) {
        if (token != null && !token.isBlank()) {
            sessionRepository.deleteById(token);
        }
    }

    private ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Credenciales de administrador incorrectas");
    }

    private ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión de administrador no válida");
    }

    private static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}