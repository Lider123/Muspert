package com.babaetskv.muspert.data.network.gateway

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.network.CommonApi
import com.babaetskv.muspert.data.network.models.AccessTokenModel
import com.babaetskv.muspert.data.prefs.PrefsHelper
import io.reactivex.Completable

/**
 * @author Konstantin on 26.06.2020
 */
class AuthGatewayImpl(
    private val commonApi: CommonApi,
    private val prefsHelper: PrefsHelper,
    private val schedulersProvider: SchedulersProvider
) : AuthGateway {

    override fun authorize(accessToken: String): Completable =
        commonApi.authorize(AccessTokenModel(accessToken))
            .flatMapCompletable {
                prefsHelper.authTokenPreference.set(it.token)
                Completable.complete()
            }
            .subscribeOn(schedulersProvider.IO)
}
