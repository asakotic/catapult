package com.example.catapult.cats.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {

    @Query("select * from cats")
    fun getAllCats(): Flow<List<Cat>>

    @Query("select * from cats where id = :id")
    fun getCatById(id: String): Flow<Cat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCats(cats: List<Cat>)
}