package com.example.catalist.cats.domen

import com.example.catalist.cats.network.api.model.Breed
import kotlinx.serialization.Serializable


//data class automatically generates `equals()`, `hashCode()`, `toString()`, and `copy()`
@Serializable
data class CatInfo(
    val id: String,
    val raceOfCat: String,
    val altNames: List<String> = emptyList(),
    val catImageUrl: String,
    val description: String,
    val countriesOfOrigin: List<String>,
    val temperamentTraits: List<String>,
    val lifeSpan: String,
    val averageWeight: String,
    val isRare: Boolean,
    val wikiUrl: String,
    val mapWidgets: Map<String, Int>

) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return raceOfCat.contains(query, true)
    }
}