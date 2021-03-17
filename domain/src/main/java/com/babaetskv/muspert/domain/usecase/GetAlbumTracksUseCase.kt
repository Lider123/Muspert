package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetAlbumTracksUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<Long, List<Track>> {

    override fun execute(params: Long): Single<List<Track>> =
        catalogRepository.getTracks(params)
}
