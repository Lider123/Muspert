package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.data.network.model.UserModel

/**
 * @author Konstantin on 26.06.2020
 */
class UserModelToUserMapper : Mapper<UserModel, User> {

    override fun map(src: UserModel?): User? = src?.let {
        User(
            id = it.id,
            nickname = it.nickname,
            firstName = it.firstName,
            lastName = it.lastName,
            avatar = it.avatar
        )
    }
}
