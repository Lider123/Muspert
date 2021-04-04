package com.babaetskv.muspert.data.db

import android.content.Context
import androidx.room.Room

class DatabaseFactory(private val context: Context) {

    fun createAppDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
            .build()
}
