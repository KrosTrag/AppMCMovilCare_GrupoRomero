package com.mcmovilcare.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {
    private AuthDtos() { }

    public record AdminLoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record AdminLoginResponse(String token, String email) { }
}