package com.babaetskv.muspert.domain.gateway

import io.reactivex.Completable

interface FavoritesGateway {

    fun addToFavorites(trackId: Long): Completable

    fun removeFromFavorites(trackId: Long): Completable
}
