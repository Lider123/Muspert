package com.babaetskv.muspert.data.network.gateway

import io.reactivex.Completable

/**
 * @author Konstantin on 26.06.2020
 */
interface AuthGateway {

    fun authorize(accessToken: String): Completable
}
