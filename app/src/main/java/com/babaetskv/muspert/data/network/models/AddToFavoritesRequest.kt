package com.babaetskv.muspert.data.network.models

import com.google.gson.annotations.SerializedName

data class AddToFavoritesRequest(
    @SerializedName("trackId") val trackId: Long
)
