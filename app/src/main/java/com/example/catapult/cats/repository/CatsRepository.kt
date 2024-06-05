package com.example.catapult.cats.repository

import com.example.catapult.cats.db.CatsService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton //TODO this singleton should be remove when database is added
class CatsRepository @Inject constructor(
    private val catsService: CatsService
){
//    //ICatListAPI:: class.java -  reflection of ICatListAPI class and instance (reference type of java)
//    //create validates interface
//    //https://stackoverflow.com/questions/68686142/what-is-the-meaning-of-class-java
//    private val catApi: ICatListAPI = retrofit.create(ICatListAPI:: class.java)
//
//    suspend fun fetchAllCatsFromApi() {
//        catsService.addAllCats(cats = catApi.getAllCats())
//    }
//
//    fun observeCat(id: String): Flow<Cat> {
//        //map because of Flow<List<T> to get List<T>
//        return observeCats().map { catList -> catList.first {cat -> cat.id == id} }.distinctUntilChanged()
//    }
//    fun observeCats(): Flow<List<Cat>> = catsService.getAllCatsStream()
//
//    suspend fun getCatById(id: String): Cat = catsService.getCatById(id)



}