package com.example.appmcmovilcare_gruporomero.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ExternalCatalogApi {
    @GET("products/category/smartphones")
    suspend fun smartphones(@Query("limit") limit: Int = 5): ProductResponse
}