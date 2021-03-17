package com.babaetskv.muspert.data.model

import com.google.gson.annotations.SerializedName

data class TrackInfoModel(
    @SerializedName("id") val id: Long,
    @SerializedName("order") val order: Int
)
