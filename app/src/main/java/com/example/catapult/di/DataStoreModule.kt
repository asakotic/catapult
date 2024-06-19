package com.example.catapult.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.catapult.users.UsersData
import com.example.catapult.users.UsersDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideUserListDataStore (@ApplicationContext context: Context) : DataStore<UsersData> {
        return DataStoreFactory.create(
            produceFile = { context.dataStoreFile(fileName = "users.json") },
            serializer = UsersDataSerializer()
        )
    }
}