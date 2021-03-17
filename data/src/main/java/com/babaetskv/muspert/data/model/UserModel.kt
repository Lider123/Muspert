package com.babaetskv.muspert.data.model

import com.google.gson.annotations.SerializedName

/**
 * @author Konstantin on 26.06.2020
 */
data class UserModel(
    @SerializedName("id") var id: Long,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("last_name") var lastName: String,
    @SerializedName("avatar") val avatar: String?
)
