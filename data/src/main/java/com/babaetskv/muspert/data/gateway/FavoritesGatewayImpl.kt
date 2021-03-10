package com.babaetskv.muspert.data.gateway

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.model.AddToFavoritesRequest
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.domain.gateway.FavoritesGateway
import io.reactivex.Completable

class FavoritesGatewayImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider
) : FavoritesGateway {

    override fun addToFavorites(trackId: Long): Completable =
        authApi.addToFavorites(AddToFavoritesRequest(trackId))
            .subscribeOn(schedulersProvider.IO)

    override fun removeFromFavorites(trackId: Long): Completable =
        authApi.removeFromFavorites(trackId)
            .subscribeOn(schedulersProvider.IO)

}
