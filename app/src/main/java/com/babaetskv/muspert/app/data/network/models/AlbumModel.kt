package com.babaetskv.muspert.app.data.network.models

import com.google.gson.annotations.SerializedName

data class AlbumModel(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("createdAt") val createdAt: Long
)
