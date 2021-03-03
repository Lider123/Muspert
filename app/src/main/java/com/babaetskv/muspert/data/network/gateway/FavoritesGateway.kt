package com.babaetskv.muspert.data.network.gateway

import io.reactivex.Completable

interface FavoritesGateway {

    fun addToFavorites(trackId: Long): Completable

    fun removeFromFavorites(trackId: Long): Completable
}
