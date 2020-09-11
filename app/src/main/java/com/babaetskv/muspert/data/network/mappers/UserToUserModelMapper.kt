package com.babaetskv.muspert.data.network.mappers

import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.models.UserModel

class UserToUserModelMapper : Mapper<User, UserModel> {

    override fun map(src: User?): UserModel? = src?.let {
        UserModel(
            id = it.id,
            nickname = it.nickname ?: "",
            firstName = it.firstName ?: "",
            lastName = it.lastName ?: ""
        )
    }
}
