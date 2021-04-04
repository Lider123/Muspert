package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.GetCacheTracksParams
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetCacheTracksUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<GetCacheTracksParams, List<Track>> {

    override fun execute(params: GetCacheTracksParams): Single<List<Track>> =
        catalogRepository.getCacheTracks(params)
}
