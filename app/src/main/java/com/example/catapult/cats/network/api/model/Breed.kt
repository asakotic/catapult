package com.example.catapult.cats.network.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Breed(
    val id: String,
    val name: String,
    @SerialName ("alt_names") val altNames: String = "",
    val temperament: String,
    val origin: String,
    val description: String,
    @SerialName("life_span") val lifeSpan: String,
    val adaptability: Int,
    @SerialName("affection_level") val affectionLevel: Int,
    @SerialName("child_friendly") val childFriendly: Int,
    @SerialName("dog_friendly") val dogFriendly: Int,
    @SerialName("energy_level") val energyLevel: Int,
    @SerialName("health_issues") val healthIssues: Int,
    val rare: Int,
    val weight: CatWeight,
    @SerialName("wikipedia_url") val wikipediaUrl: String = "",
    val image: CatImage? = null

)

@Serializable
data class CatWeight (
    val metric: String
)

@Serializable
data class CatImage(
    val url: String
)
