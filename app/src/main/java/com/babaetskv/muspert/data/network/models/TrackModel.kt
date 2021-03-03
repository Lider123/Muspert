package com.babaetskv.muspert.data.network.models

import com.google.gson.annotations.SerializedName

data class TrackModel(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("albumId") val albumId: Long,
    @SerializedName("position") val position: Int,
    @SerializedName("cover") val cover: String,
    @SerializedName("albumTitle") val albumTitle: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("isFavorite") val isFavorite: Boolean
)
