package com.example.catapult.cats.db.images

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatGalleryDao {

    @Query("select url from images where id= :id")
    fun getAllImagesForId(id: String): Flow<List<String>>

    @Query("select url from images where url = :url")
    fun getImageByUrl(url: String): Flow<String>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGalleryCats(cats: List<CatGallery>)
}