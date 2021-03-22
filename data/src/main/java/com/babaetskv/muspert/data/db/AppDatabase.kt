package com.babaetskv.muspert.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.babaetskv.muspert.data.db.dao.TrackDao
import com.babaetskv.muspert.data.db.entity.TrackEntity

@Database(entities = [TrackEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}