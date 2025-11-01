package com.example.bai1.network

import com.example.bai1.Product
import retrofit2.http.GET

interface ApiService {
    @GET("product")
    suspend fun getProduct(): Product
}
