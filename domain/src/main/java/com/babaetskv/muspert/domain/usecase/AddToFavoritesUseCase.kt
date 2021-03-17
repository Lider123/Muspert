package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.gateway.FavoritesGateway
import com.babaetskv.muspert.domain.usecase.base.CompletableUseCaseWithParams
import io.reactivex.Completable

class AddToFavoritesUseCase(
    private val favoritesGateway: FavoritesGateway
) : CompletableUseCaseWithParams<Long> {

    override fun execute(params: Long): Completable =
        favoritesGateway.addToFavorites(params)
}
