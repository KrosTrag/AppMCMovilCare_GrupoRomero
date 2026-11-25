package com.mcmovilcare.backend.service;

import com.mcmovilcare.backend.api.TicketDtos.AdminLoginRequest;
import com.mcmovilcare.backend.api.TicketDtos.AdminLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

@Service
public class AdminAuthService {
    private final RestClient authService;

    public AdminAuthService(RestClient.Builder restClientBuilder,
                            @Value("${app.auth-service.url}") String authServiceUrl) {
        this.authService = restClientBuilder.baseUrl(authServiceUrl).build();
    }

    public AdminLoginResponse iniciarSesion(String email, String password) {
        return ejecutar(() -> authService.post()
                .uri("/api/auth/admin")
                .body(new AdminLoginRequest(email, password))
                .retrieve()
                .body(AdminLoginResponse.class));
    }

    public void validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión de administrador no válida");
        }
        ejecutar(() -> authService.post()
                .uri("/internal/sessions/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity());
    }

    public void cerrarSesion(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        ejecutar(() -> authService.post()
                .uri("/api/auth/admin/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity());
    }

    private <T> T ejecutar(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientResponseException exception) {
            HttpStatus status = exception.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()
                    ? HttpStatus.UNAUTHORIZED
                    : HttpStatus.SERVICE_UNAVAILABLE;
            throw new ResponseStatusException(status, "No fue posible completar la solicitud de autenticación");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Servicio de autenticación no disponible");
        }
    }
}