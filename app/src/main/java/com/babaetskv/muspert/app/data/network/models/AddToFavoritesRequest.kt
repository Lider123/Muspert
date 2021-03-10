package com.babaetskv.muspert.app.data.network.models

import com.google.gson.annotations.SerializedName

data class AddToFavoritesRequest(
    @SerializedName("trackId") val trackId: Long
)
