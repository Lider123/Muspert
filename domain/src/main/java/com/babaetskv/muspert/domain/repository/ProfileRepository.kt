package com.babaetskv.muspert.domain.repository

import android.net.Uri
import com.babaetskv.muspert.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileRepository {

    fun getProfile(): Single<User>

    fun updateProfile(profile: User): Completable

    fun updateAvatar(imageUri: Uri): Completable
}
