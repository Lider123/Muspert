package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.models.User
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileRepository {

    fun syncProfile(): Completable

    fun getProfile(): Single<User>
}
