package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.TrackInfo
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetAlbumTrackInfosUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<Long, List<TrackInfo>> {

    override fun execute(params: Long): Single<List<TrackInfo>> =
        catalogRepository.getTrackInfos(params)
}
