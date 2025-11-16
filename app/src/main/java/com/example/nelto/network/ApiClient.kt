package com.example.nelto.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // IMPORTANTE: Cambia por tu IP local, NO uses "localhost"
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}