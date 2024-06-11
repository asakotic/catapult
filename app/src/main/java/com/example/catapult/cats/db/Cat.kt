package com.example.catapult.cats.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "cats")
@Serializable
data class Cat (
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo ("alt_names")
    @SerialName ("alt_names")
    val altNames: String? = null,
    val temperament: String,
    val origin: String,
    val description: String,
    @ColumnInfo("life_span")
    @SerialName("life_span")
    val lifeSpan: String,
    val adaptability: Int,
    @ColumnInfo("affection_level")
    @SerialName("affection_level")
    val affectionLevel: Int,
    @ColumnInfo("child_friendly")
    @SerialName("child_friendly")
    val childFriendly: Int,
    @ColumnInfo("dog_friendly")
    @SerialName("dog_friendly")
    val dogFriendly: Int,
    @ColumnInfo("energy_level")
    @SerialName("energy_level")
    val energyLevel: Int,
    @ColumnInfo("health_issues")
    @SerialName("health_issues")
    val healthIssues: Int,
    val rare: Int,
    @Embedded
    val weight: CatWeight,
    @ColumnInfo("wikipedia_url")
    @SerialName("wikipedia_url")
    val wikipediaUrl: String? = null,
    @Embedded
    val image: CatImage? = null
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return name.contains(query, true)
    }

    fun averageWeight():Double {
        val lines = weight.metric.replace(" ", "").split("-")
        return lines[0].toDouble() / lines[1].toDouble()
    }

    fun averageLifeSpan(): Double {
        val lines = lifeSpan.replace(" ", "").split("-")
        return lines[0].toDouble() / lines[1].toDouble()
    }

    fun getListOfTemperaments(): List<String> {
        return temperament.replace(" ", "").split(",")
    }
}

@Serializable
data class CatWeight (
    val metric: String
)

@Serializable
data class CatImage(
    val url: String
)