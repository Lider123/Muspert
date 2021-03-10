package com.babaetskv.muspert.app.data.network.models

import com.google.gson.annotations.SerializedName

data class TrackInfoModel(
    @SerializedName("id") val id: Long,
    @SerializedName("order") val order: Int
)
