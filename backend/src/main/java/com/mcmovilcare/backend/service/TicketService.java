package com.mcmovilcare.backend.service;

import com.mcmovilcare.backend.api.TicketDtos.CreateTicketRequest;
import com.mcmovilcare.backend.api.TicketDtos.EditProblemRequest;
import com.mcmovilcare.backend.api.TicketDtos.TicketResponse;
import com.mcmovilcare.backend.domain.Ticket;
import com.mcmovilcare.backend.domain.TicketRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TicketService {
    private static final List<String> ESTADOS = List.of(
            "Ingresado", "En Diagnóstico", "Esperando Autorización", "Reparado", "Entregado"
    );

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public TicketResponse crear(CreateTicketRequest request) {
        String id = "OT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        Ticket ticket = new Ticket(id, request.clienteNombre().trim(), request.correoCliente().trim(),
                request.equipoModelo().trim(), request.numeroSerie().trim(), request.diagnostico().trim(),
                "Ingresado", true);
        return respuesta(repository.save(ticket));
    }

    public TicketResponse buscarParaCliente(String id, String email) {
        Ticket ticket = buscar(id);
        validarPropietario(ticket, email);
        return respuesta(ticket);
    }

    public TicketResponse editarProblema(String id, String email, EditProblemRequest request) {
        Ticket ticket = buscar(id);
        validarPropietario(ticket, email);
        if (request.diagnostico().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El problema no puede quedar vacío");
        }
        ticket.setDiagnostico(request.diagnostico().trim());
        return respuesta(repository.save(ticket));
    }

    public List<TicketResponse> listarParaAdministrador() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "creadoEn"))
                .stream().map(TicketService::respuesta).toList();
    }

    public TicketResponse cambiarEstado(String id, String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de solicitud no válido");
        }
        Ticket ticket = buscar(id);
        ticket.setEstado(estado);
        return respuesta(repository.save(ticket));
    }

    public void eliminar(String id) {
        repository.delete(buscar(id));
    }

    private Ticket buscar(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
    }

    private void validarPropietario(Ticket ticket, String email) {
        if (email == null || !ticket.getCorreoCliente().trim().equalsIgnoreCase(email.trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El correo no corresponde a esta solicitud");
        }
    }

    private static TicketResponse respuesta(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getClienteNombre(), ticket.getCorreoCliente(),
                ticket.getEquipoModelo(), ticket.getNumeroSerie(), ticket.getDiagnostico(), ticket.getEstado(),
                ticket.isRequiereAutorizacion(), ticket.getCreadoEn());
    }
}