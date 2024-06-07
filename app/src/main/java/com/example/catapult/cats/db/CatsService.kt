package com.example.catapult.cats.db

import com.example.catapult.cats.network.api.ICatListAPI
import com.example.catapult.cats.network.retrofit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CatsService @Inject constructor(
    private val catDao: CatDao,
    private val catGalleryDao: CatGalleryDao
) {
    //ICatListAPI:: class.java -  reflection of ICatListAPI class and instance (reference type of java)
    //create validates interface
    //https://stackoverflow.com/questions/68686142/what-is-the-meaning-of-class-java
    private val catApi: ICatListAPI = retrofit.create(ICatListAPI:: class.java)

    suspend fun fetchAllCatsFromApi() {
        catDao.insertAllCats(cats = catApi.getAllCats())
    }

    fun getAllCatsFlow(): Flow<List<Cat>> = catDao.getAllCats()

    fun getCatByIdFlow(id: String): Flow<Cat> = catDao.getCatById(id)

    suspend fun getAllCatsPhotosApi(id: String){
        catGalleryDao.insertAllGalleryCats(cats = catApi.getAllCatsPhotos(id).map { it.copy(id = id) })
    }

    fun getAllCatImagesFlow(id: String): Flow<List<String>> = catGalleryDao.getAllImagesForId(id)

}