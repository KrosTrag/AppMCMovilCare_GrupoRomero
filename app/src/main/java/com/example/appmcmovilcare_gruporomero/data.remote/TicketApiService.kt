package com.example.appmcmovilcare_gruporomero.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketApiService {
    @POST("api/tickets")
    suspend fun crear(@Body request: CreateTicketRequest): TicketDto

    @GET("api/tickets/{id}")
    suspend fun buscarParaCliente(@Path("id") id: String, @Query("email") email: String): TicketDto

    @PUT("api/tickets/{id}")
    suspend fun editarProblema(
        @Path("id") id: String,
        @Query("email") email: String,
        @Body request: EditProblemRequest
    ): TicketDto

    @POST("api/auth/admin")
    suspend fun iniciarSesion(@Body request: AdminLoginRequest): AdminLoginResponse

    @POST("api/auth/admin/logout")
    suspend fun cerrarSesion(@Header("Authorization") authorization: String)

    @GET("api/admin/tickets")
    suspend fun listarAdministrador(@Header("Authorization") authorization: String): List<TicketDto>

    @PATCH("api/admin/tickets/{id}/status")
    suspend fun cambiarEstado(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body request: StatusRequest
    ): TicketDto

    @DELETE("api/admin/tickets/{id}")
    suspend fun eliminar(@Header("Authorization") authorization: String, @Path("id") id: String)
}