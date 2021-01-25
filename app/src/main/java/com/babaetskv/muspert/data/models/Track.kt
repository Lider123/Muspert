package com.babaetskv.muspert.data.models

data class Track(
    val id: Long,
    val title: String,
    val link: String,
    val albumId: Long,
    val cover: String,
    val albumTitle: String
)
