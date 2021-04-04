package com.babaetskv.muspert.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.babaetskv.muspert.data.db.entity.TrackEntity
import io.reactivex.Single

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks ORDER BY created_at DESC")
    fun getAll(): Single<List<TrackEntity>>
}
