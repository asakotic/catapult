package com.example.catapult.cats.network

import com.example.catapult.cats.network.serialization.JsonAndClass
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

val client = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request().newBuilder()
            .addHeader("x-api-key", "live_Z26o8l5prRJqZJoRO8G8Z6E3Dsq8JyPxgI6NXmLBzrKZ5x3V5vzNLmqYW7CbgZka")
            .build()
        it.proceed(request)
    }
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    )
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.thecatapi.com/v1/")
    .client(client)
    .addConverterFactory(JsonAndClass.asConverterFactory("application/json".toMediaType()))
    .build()