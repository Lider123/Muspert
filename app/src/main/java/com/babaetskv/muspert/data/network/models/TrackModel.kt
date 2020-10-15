package com.babaetskv.muspert.data.network.models

import com.google.gson.annotations.SerializedName

data class TrackModel(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("albumId") val albumId: Long
)
