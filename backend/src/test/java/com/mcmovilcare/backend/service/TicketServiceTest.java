package com.mcmovilcare.backend.service;

import com.mcmovilcare.backend.api.TicketDtos.CreateTicketRequest;
import com.mcmovilcare.backend.api.TicketDtos.EditProblemRequest;
import com.mcmovilcare.backend.domain.Ticket;
import com.mcmovilcare.backend.domain.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class TicketServiceTest {
    private TicketRepository repository;
    private TicketService service;

    @BeforeEach
    void setUp() {
        repository = mock(TicketRepository.class);
        service = new TicketService(repository);
    }

    @Test
    void creaTicketEnEstadoIngresado() {
        when(repository.save(any(Ticket.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.crear(new CreateTicketRequest("Ana", "ana@example.com", "Laptop", "SN-1", "No enciende"));

        assertEquals("Ingresado", result.estado());
        assertEquals("ana@example.com", result.correoCliente());
    }

    @Test
    void permiteAlPropietarioConsultarYEditarElProblema() {
        Ticket ticket = ticket();
        when(repository.findById("OT-1")).thenReturn(Optional.of(ticket));
        when(repository.save(ticket)).thenReturn(ticket);

        assertEquals("OT-1", service.buscarParaCliente("OT-1", " ANA@example.com ").id());
        var result = service.editarProblema("OT-1", "ana@example.com", new EditProblemRequest("Pantalla rota"));

        assertEquals("Pantalla rota", result.diagnostico());
    }

    @Test
    void rechazaEdicionDeOtroCorreoYProblemaVacio() {
        when(repository.findById("OT-1")).thenReturn(Optional.of(ticket()));

        assertThrows(ResponseStatusException.class,
                () -> service.editarProblema("OT-1", "otro@example.com", new EditProblemRequest("Cambio")));
        assertThrows(ResponseStatusException.class,
                () -> service.editarProblema("OT-1", "ana@example.com", new EditProblemRequest(" ")));
    }

    @Test
    void rechazaSolicitudInexistente() {
        when(repository.findById("OT-missing")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.buscarParaCliente("OT-missing", "ana@example.com"));
    }

    @Test
    void validaCambioDeEstadoYEliminacion() {
        Ticket ticket = ticket();
        when(repository.findById("OT-1")).thenReturn(Optional.of(ticket));
        when(repository.save(ticket)).thenReturn(ticket);

        assertEquals("Reparado", service.cambiarEstado("OT-1", "Reparado").estado());
        assertThrows(ResponseStatusException.class, () -> service.cambiarEstado("OT-1", "Falso"));
        service.eliminar("OT-1");
        verify(repository).delete(ticket);
    }

    @Test
    void listaSolicitudesOrdenadasPorFecha() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(ticket()));

        assertEquals(1, service.listarParaAdministrador().size());
    }

    private static Ticket ticket() {
        return new Ticket("OT-1", "Ana", "ana@example.com", "Laptop", "SN-1", "No enciende", "Ingresado", true);
    }
}