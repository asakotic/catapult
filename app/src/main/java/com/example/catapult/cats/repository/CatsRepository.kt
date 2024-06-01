package com.example.catapult.cats.repository

import com.example.catapult.cats.domen.CatInfo
import com.example.catapult.cats.network.api.ICatListAPI
import com.example.catapult.cats.network.api.model.Breed
import com.example.catapult.cats.network.retrofit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object CatsRepository {

    private val cats = MutableStateFlow(listOf<CatInfo>())
    //ICatListAPI:: class.java -  reflection of ICatListAPI class and instance (reference type of java)
    //create validates interface
    //https://stackoverflow.com/questions/68686142/what-is-the-meaning-of-class-java
    private val catApi: ICatListAPI = retrofit.create(ICatListAPI:: class.java)

    private fun breedToCatInfo(breeds: Breed): CatInfo {
        val widgets = mapOf(
            "affectionLevel" to breeds.affectionLevel,
            "childFriendly" to breeds.childFriendly,
            "dogFriendly" to breeds.dogFriendly,
            "energyLevel" to breeds.energyLevel,
            "healthIssues" to breeds.healthIssues
        )

        return CatInfo(
            id = breeds.id,
            raceOfCat = breeds.name,
            altNames = if (breeds.altNames ==  "") emptyList() else  breeds.altNames.split(","),
            catImageUrl = breeds.image?.url ?: "",
            description = breeds.description,
            lifeSpan = breeds.lifeSpan,
            countriesOfOrigin = if (breeds.origin == "") emptyList() else breeds.origin.split(","),
            averageWeight = breeds.weight.metric,
            isRare = breeds.rare != 0,
            wikiUrl = breeds.wikipediaUrl,
            temperamentTraits = if (breeds.temperament == "") emptyList() else  breeds.temperament.split(","),
            mapWidgets = widgets
        )
    }

    suspend fun fetchAllCats() {
        cats.update { catApi.getAllCats().map { breedToCatInfo(it) } }
    }

    fun observeCat(id: String): Flow<CatInfo> {
        //map because of Flow<List<T> to get List<T>
        return observeCats().map { catList -> catList.first {cat -> cat.id == id} }.distinctUntilChanged()
    }
    fun observeCats(): Flow<List<CatInfo>> = cats.asStateFlow()

    suspend fun getCatById(id: String): CatInfo = breedToCatInfo(catApi.getCat(id))



}