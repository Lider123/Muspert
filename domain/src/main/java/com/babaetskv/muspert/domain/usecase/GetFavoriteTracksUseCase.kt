package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.GetFavoriteTracksParams
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetFavoriteTracksUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<GetFavoriteTracksParams, List<Track>> {

    override fun execute(params: GetFavoriteTracksParams): Single<List<Track>> =
        catalogRepository.getFavoriteTracks(params)
}
