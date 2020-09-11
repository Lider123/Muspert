package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.mappers.UserModelToUserMapper
import com.babaetskv.muspert.data.network.mappers.UserToUserModelMapper
import io.reactivex.Completable
import io.reactivex.Single

class ProfileRepositoryImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider,
    private val userModelToUserMapper: UserModelToUserMapper,
    private val userToUserModelMapper: UserToUserModelMapper
) : ProfileRepository {

    override fun getProfile(): Single<User> =
        authApi.getUser()
            .subscribeOn(schedulersProvider.IO)
            .map(userModelToUserMapper::map)

    override fun updateProfile(profile: User): Completable =
        userToUserModelMapper.map(profile)!!.let {
            authApi.updateUser(it)
        }
            .subscribeOn(schedulersProvider.IO)
}
