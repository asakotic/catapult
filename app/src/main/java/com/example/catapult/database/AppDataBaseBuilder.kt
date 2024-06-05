package com.example.catapult.database

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppDataBaseBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun build(): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "catapult"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}