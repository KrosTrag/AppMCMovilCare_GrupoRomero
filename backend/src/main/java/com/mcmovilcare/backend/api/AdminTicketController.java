package com.mcmovilcare.backend.api;

import com.mcmovilcare.backend.api.TicketDtos.StatusRequest;
import com.mcmovilcare.backend.api.TicketDtos.TicketResponse;
import com.mcmovilcare.backend.service.AdminAuthService;
import com.mcmovilcare.backend.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminTicketController {
    private final AdminAuthService authService;
    private final TicketService ticketService;

    public AdminTicketController(AdminAuthService authService, TicketService ticketService) {
        this.authService = authService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<TicketResponse> listar(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.validarToken(AdminAuthController.token(authorization));
        return ticketService.listarParaAdministrador();
    }

    @PatchMapping("/{id}/status")
    public TicketResponse cambiarEstado(@RequestHeader(value = "Authorization", required = false) String authorization,
                                        @PathVariable String id, @Valid @RequestBody StatusRequest request) {
        authService.validarToken(AdminAuthController.token(authorization));
        return ticketService.cambiarEstado(id, request.estado());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@RequestHeader(value = "Authorization", required = false) String authorization,
                         @PathVariable String id) {
        authService.validarToken(AdminAuthController.token(authorization));
        ticketService.eliminar(id);
    }
}