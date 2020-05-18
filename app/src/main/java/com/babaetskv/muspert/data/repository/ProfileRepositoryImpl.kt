package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.mappers.UserModelToUserMapper
import io.reactivex.Single

class ProfileRepositoryImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider,
    private val userModelToUserMapper: UserModelToUserMapper
) : ProfileRepository {

    override fun getProfile(): Single<User> =
        authApi.getUser()
            .subscribeOn(schedulersProvider.IO)
            .map(userModelToUserMapper::map)
}
