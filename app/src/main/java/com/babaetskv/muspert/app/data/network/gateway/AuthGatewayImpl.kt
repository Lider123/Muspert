package com.babaetskv.muspert.app.data.network.gateway

import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.app.data.network.CommonApi
import com.babaetskv.muspert.app.data.network.models.AccessTokenModel
import com.babaetskv.muspert.domain.gateway.AuthGateway
import com.babaetskv.muspert.domain.prefs.AppPrefs
import io.reactivex.Completable

/**
 * @author Konstantin on 26.06.2020
 */
class AuthGatewayImpl(
    private val commonApi: CommonApi,
    private val schedulersProvider: SchedulersProvider,
    private val appPrefs: AppPrefs
) : AuthGateway {

    override fun authorize(accessToken: String): Completable =
        commonApi.authorize(AccessTokenModel(accessToken))
            .flatMapCompletable {
                appPrefs.authToken = it.token
                Completable.complete()
            }
            .subscribeOn(schedulersProvider.IO)

    override fun logout(): Completable =
        Completable.fromAction {
            appPrefs.reset()
        }
            .subscribeOn(schedulersProvider.IO)
}
