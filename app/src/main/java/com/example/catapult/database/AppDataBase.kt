package com.example.catapult.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.catapult.cats.db.Cat
import com.example.catapult.cats.db.CatDao
import com.example.catapult.cats.db.images.CatGallery
import com.example.catapult.cats.db.images.CatGalleryDao

@Database(
    entities = [
        Cat::class, CatGallery::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)
abstract class AppDataBase: RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun catGalleryDao(): CatGalleryDao
}