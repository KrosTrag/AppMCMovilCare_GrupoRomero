package com.mcmovilcare.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    private String id;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private String correoCliente;

    @Column(nullable = false)
    private String equipoModelo;

    @Column(nullable = false)
    private String numeroSerie;

    @Column(nullable = false, length = 2000)
    private String diagnostico;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private boolean requiereAutorizacion;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    protected Ticket() {
    }

    public Ticket(String id, String clienteNombre, String correoCliente, String equipoModelo,
                  String numeroSerie, String diagnostico, String estado,
                  boolean requiereAutorizacion) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.correoCliente = correoCliente;
        this.equipoModelo = equipoModelo;
        this.numeroSerie = numeroSerie;
        this.diagnostico = diagnostico;
        this.estado = estado;
        this.requiereAutorizacion = requiereAutorizacion;
    }

    @PrePersist
    void asignarFechaCreacion() {
        if (creadoEn == null) {
            creadoEn = Instant.now();
        }
    }

    public String getId() { return id; }
    public String getClienteNombre() { return clienteNombre; }
    public String getCorreoCliente() { return correoCliente; }
    public String getEquipoModelo() { return equipoModelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public String getDiagnostico() { return diagnostico; }
    public String getEstado() { return estado; }
    public boolean isRequiereAutorizacion() { return requiereAutorizacion; }
    public Instant getCreadoEn() { return creadoEn; }

    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public void setEstado(String estado) { this.estado = estado; }
}