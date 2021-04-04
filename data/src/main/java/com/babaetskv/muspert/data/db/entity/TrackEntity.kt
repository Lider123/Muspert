package com.babaetskv.muspert.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "album_id") val albumId: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "cover_path") val coverPath: String,
    @ColumnInfo(name = "album_title") val albumTitle: String,
    @ColumnInfo(name = "artist_name") val artistName: String
)