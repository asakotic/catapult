package com.example.catapult.cats.network.api

import com.example.catapult.cats.db.Cat
import retrofit2.http.GET
import retrofit2.http.Path

interface ICatListAPI {
    @GET("breeds")
    suspend fun getAllCats(): List<Cat>

    @GET("breeds/{id}")
    suspend fun getCat(@Path("id") id: String): Cat
}