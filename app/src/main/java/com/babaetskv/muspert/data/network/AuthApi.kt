package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.network.models.UserModel
import io.reactivex.Single
import retrofit2.http.GET

/**
 * @author Konstantin on 25.06.2020
 */
interface AuthApi {

    @GET("api/profile")
    fun getUser(): Single<UserModel>
}
