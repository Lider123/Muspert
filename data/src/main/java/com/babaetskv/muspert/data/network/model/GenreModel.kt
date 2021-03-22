package com.babaetskv.muspert.data.network.model

import com.google.gson.annotations.SerializedName

data class GenreModel(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String
)
