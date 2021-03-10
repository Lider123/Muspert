package com.babaetskv.muspert.app.data.network.models

import com.google.gson.annotations.SerializedName

/**
 * @author Konstantin on 26.06.2020
 */
data class AuthTokenModel(
    @SerializedName("token") val token: String
)
