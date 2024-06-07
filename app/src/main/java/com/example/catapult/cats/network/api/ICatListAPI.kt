package com.example.catapult.cats.network.api

import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.images.CatGallery
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ICatListAPI {
    @GET("breeds")
    suspend fun getAllCats(): List<Cat>

    @GET("breeds/{id}")
    suspend fun getCat(@Path("id") id: String): Cat

    @GET("images/search?limit=20")
    suspend fun getAllCatsPhotos(@Query("breed_ids") id: String): List<CatGallery>

}