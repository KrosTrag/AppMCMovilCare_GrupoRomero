package com.example.appmcmovilcare_gruporomero.data.remote

import com.example.appmcmovilcare_gruporomero.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val backendRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val catalogRetrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tickets: TicketApiService = backendRetrofit.create(TicketApiService::class.java)
    val catalog: ExternalCatalogApi = catalogRetrofit.create(ExternalCatalogApi::class.java)
}