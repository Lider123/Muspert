package com.babaetskv.muspert.data.model

import com.google.gson.annotations.SerializedName

/**
 * @author Konstantin on 26.06.2020
 */
data class AccessTokenModel(
    @SerializedName("access_token") val token: String
)
