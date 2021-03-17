package com.babaetskv.muspert.data.model

import com.google.gson.annotations.SerializedName

data class AddToFavoritesRequest(
    @SerializedName("trackId") val trackId: Long
)
