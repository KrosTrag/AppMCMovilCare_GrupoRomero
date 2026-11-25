package com.example.appmcmovilcare_gruporomero.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordenes_trabajo")
data class OrdenTrabajoEntity(
    @PrimaryKey val id: String,
    val clienteNombre: String,
    val correoCliente: String,
    val equipoModelo: String,
    val numeroSerie: String,
    val diagnostico: String,
    val estado: String,
    val requiereAutorizacion: Boolean,
    val imagenEvidenciaUri: String? = null
)