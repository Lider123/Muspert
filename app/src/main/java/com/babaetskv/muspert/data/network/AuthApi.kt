package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.network.models.UserModel
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author Konstantin on 25.06.2020
 */
interface AuthApi {

    @GET("api/profile")
    fun getUser(): Single<UserModel>

    @POST("api/profile")
    fun updateUser(
        @Body profile: UserModel
    ): Completable
}
