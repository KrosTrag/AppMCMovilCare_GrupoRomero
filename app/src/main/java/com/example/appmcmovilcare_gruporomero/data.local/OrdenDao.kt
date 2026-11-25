package com.example.appmcmovilcare_gruporomero.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenDao {
    @Query("SELECT * FROM ordenes_trabajo")
    fun obtenerTodasLasOrdenes(): Flow<List<OrdenTrabajoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOrden(orden: OrdenTrabajoEntity)

    @Update
    suspend fun actualizarOrden(orden: OrdenTrabajoEntity)

    @Delete
    suspend fun eliminarOrden(orden: OrdenTrabajoEntity)

    @Query("DELETE FROM ordenes_trabajo WHERE id = :id")
    suspend fun eliminarPorId(id: String)
}