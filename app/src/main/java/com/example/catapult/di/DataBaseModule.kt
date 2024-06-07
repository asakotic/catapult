package com.example.catapult.di

import com.example.catapult.cats.db.CatDao
import com.example.catapult.cats.db.images.CatGalleryDao
import com.example.catapult.database.AppDataBase
import com.example.catapult.database.AppDataBaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //class in which you can add bindings for types that cannot be constructor injected
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    fun provideCatDao(dataBase: AppDataBase): CatDao = dataBase.catDao()
    @Provides
    fun provideCatGalleryDao(dataBase: AppDataBase): CatGalleryDao = dataBase.catGalleryDao()

    @Provides
    @Singleton
    fun provideDatabase(appDataBaseBuilder: AppDataBaseBuilder): AppDataBase = appDataBaseBuilder.build()
}