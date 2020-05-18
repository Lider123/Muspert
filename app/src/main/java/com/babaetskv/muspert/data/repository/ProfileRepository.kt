package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.models.User
import io.reactivex.Single

interface ProfileRepository {

    fun getProfile(): Single<User>
}
