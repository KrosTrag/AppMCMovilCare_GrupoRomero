package com.mcmovilcare.auth.api;

import com.mcmovilcare.auth.service.AdminAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/sessions")
public class InternalSessionController {
    private final AdminAuthService authService;

    public InternalSessionController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void validar(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.validarToken(AdminAuthController.token(authorization));
    }
}