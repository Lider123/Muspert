package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import io.reactivex.Completable
import io.reactivex.Single

class ProfileRepositoryImpl(
    private val schedulersProvider: SchedulersProvider
) : ProfileRepository {

    override fun syncProfile(): Completable = Completable.complete() // TODO: sync profile
        .subscribeOn(schedulersProvider.IO)

    override fun getProfile(): Single<User> =
        User(
            id = 1,
            username = "johndoe",
            firstName = "John",
            lastName = "Doe"
        ).let { Single.just(it) } // TODO: get profile
            .subscribeOn(schedulersProvider.IO)
}
