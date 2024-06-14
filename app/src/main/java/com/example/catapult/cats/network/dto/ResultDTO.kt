package com.example.catapult.cats.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResultDTO(
    val nickname: String,
    val result: Float,
    val category: Int
)