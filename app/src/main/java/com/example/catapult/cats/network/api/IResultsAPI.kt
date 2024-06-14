package com.example.catapult.cats.network.api

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.example.catapult.cats.network.dto.ResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IResultsAPI {
    @GET("leaderboard")
    suspend fun getAllResultsForCategory(@Query("category") category: Int): List<ResultDTO>

    @POST("leaderboard")
    suspend fun postResult(@Body obj:ResultDTO)
}