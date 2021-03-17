package com.babaetskv.muspert.domain.gateway

import io.reactivex.Completable

/**
 * @author Konstantin on 26.06.2020
 */
interface AuthGateway {

    fun authorize(accessToken: String): Completable

    fun logout(): Completable
}
