package com.example.catapult.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.CatDao

@Database(entities = [Cat::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    abstract fun catDao(): CatDao
}