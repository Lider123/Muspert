package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.data.model.UserModel

class UserToUserModelMapper : Mapper<User, UserModel> {

    override fun map(src: User?): UserModel? = src?.let {
        UserModel(
            id = it.id,
            nickname = it.nickname ?: "",
            firstName = it.firstName ?: "",
            lastName = it.lastName ?: "",
            avatar = it.avatar
        )
    }
}
