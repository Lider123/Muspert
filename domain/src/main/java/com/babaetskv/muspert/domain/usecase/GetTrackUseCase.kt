package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetTrackUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<Long, Track> {

    override fun execute(params: Long): Single<Track> =
        catalogRepository.getTrack(params)
}
