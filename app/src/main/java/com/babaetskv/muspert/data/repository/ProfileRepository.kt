package com.babaetskv.muspert.data.repository

import android.net.Uri
import com.babaetskv.muspert.data.models.User
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileRepository {

    fun getProfile(): Single<User>

    fun updateProfile(profile: User): Completable

    fun updateAvatar(imageUri: Uri): Completable
}
