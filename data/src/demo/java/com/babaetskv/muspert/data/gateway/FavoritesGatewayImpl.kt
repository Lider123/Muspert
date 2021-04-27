package com.babaetskv.muspert.data.gateway

import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.data.model.AddToFavoritesRequest
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.domain.gateway.FavoritesGateway
import io.reactivex.Completable

class FavoritesGatewayImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider
) : FavoritesGateway {

    override fun addToFavorites(trackId: Long): Completable =
        Completable.complete()
            .subscribeOn(schedulersProvider.IO)

    override fun removeFromFavorites(trackId: Long): Completable =
        Completable.complete()
            .subscribeOn(schedulersProvider.IO)

}
