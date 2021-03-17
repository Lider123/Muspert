package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.model.AccessTokenModel
import com.babaetskv.muspert.data.model.AuthTokenModel
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author Konstantin on 25.06.2020
 */
interface CommonApi {

    @POST("api/authorization")
    fun authorize(
        @Body body: AccessTokenModel
    ): Single<AuthTokenModel>
}
