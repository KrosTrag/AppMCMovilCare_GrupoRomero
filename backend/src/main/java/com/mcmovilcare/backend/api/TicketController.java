package com.mcmovilcare.backend.api;

import com.mcmovilcare.backend.api.TicketDtos.CreateTicketRequest;
import com.mcmovilcare.backend.api.TicketDtos.EditProblemRequest;
import com.mcmovilcare.backend.api.TicketDtos.TicketResponse;
import com.mcmovilcare.backend.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse crear(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.crear(request);
    }

    @GetMapping("/{id}")
    public TicketResponse buscarParaCliente(@PathVariable String id, @RequestParam String email) {
        return ticketService.buscarParaCliente(id, email);
    }

    @PutMapping("/{id}")
    public TicketResponse editarProblema(@PathVariable String id, @RequestParam String email,
                                         @Valid @RequestBody EditProblemRequest request) {
        return ticketService.editarProblema(id, email, request);
    }
}