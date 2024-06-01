package com.example.catalist.cats.network.api

import com.example.catalist.cats.network.api.model.Breed
import retrofit2.http.GET
import retrofit2.http.Path

interface ICatListAPI {
    @GET("breeds")
    suspend fun getAllCats(): List<Breed>

    @GET("breeds/{id}")
    suspend fun getCat(@Path("id") id: String): Breed
}