package com.mcmovilcare.backend.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public final class TicketDtos {
    private TicketDtos() { }

    public record CreateTicketRequest(
            @NotBlank String clienteNombre,
            @NotBlank @Email String correoCliente,
            @NotBlank String equipoModelo,
            @NotBlank String numeroSerie,
            @NotBlank String diagnostico
    ) { }

    public record EditProblemRequest(@NotBlank String diagnostico) { }
    public record StatusRequest(@NotBlank String estado) { }
    public record AdminLoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record AdminLoginResponse(String token, String email) { }
    public record TicketResponse(
            String id,
            String clienteNombre,
            String correoCliente,
            String equipoModelo,
            String numeroSerie,
            String diagnostico,
            String estado,
            boolean requiereAutorizacion,
            Instant creadoEn
    ) { }
}