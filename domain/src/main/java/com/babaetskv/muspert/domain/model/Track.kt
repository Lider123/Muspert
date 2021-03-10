package com.babaetskv.muspert.domain.model

data class Track(
    val id: Long,
    val title: String,
    val link: String,
    val albumId: Long,
    val position: Int,
    val cover: String,
    val albumTitle: String,
    val artistName: String,
    var isFavorite: Boolean
) {

    fun toTrackInfo() =
        TrackInfo(
            id = id,
            order = position
        )
}
