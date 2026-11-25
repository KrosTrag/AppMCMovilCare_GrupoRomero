package com.mcmovilcare.backend.api;

import com.mcmovilcare.backend.api.TicketDtos.AdminLoginRequest;
import com.mcmovilcare.backend.api.TicketDtos.AdminLoginResponse;
import com.mcmovilcare.backend.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {
    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public AdminLoginResponse iniciarSesion(@Valid @RequestBody AdminLoginRequest request) {
        return authService.iniciarSesion(request.email(), request.password());
    }

    @PostMapping("/logout")
    public void cerrarSesion(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.cerrarSesion(token(authorization));
    }

    static String token(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }
}