package com.babaetskv.muspert.data.repository

import android.net.Uri
import com.babaetskv.muspert.data.mappers.UserModelToUserMapper
import com.babaetskv.muspert.data.mappers.UserToUserModelMapper
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.repository.ProfileRepository
import io.reactivex.Completable
import io.reactivex.Single

class ProfileRepositoryImpl(
    authApi: AuthApi,
    schedulersProvider: SchedulersProvider,
    userModelToUserMapper: UserModelToUserMapper,
    userToUserModelMapper: UserToUserModelMapper
) : ProfileRepository {

    override fun getProfile(): Single<User> =
        User(
            id = 1,
            nickname = "johndoe",
            firstName = "John",
            lastName = "Doe",
            avatar = "https://images.unsplash.com/photo-1593642702749-b7d2a804fbcf?ixid=MXwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80"
        ).let {
            Single.just(it)
        }

    override fun updateAvatar(imageUri: Uri): Completable =
        Completable.complete()

    override fun updateProfile(profile: User): Completable =
        Completable.complete()
}
