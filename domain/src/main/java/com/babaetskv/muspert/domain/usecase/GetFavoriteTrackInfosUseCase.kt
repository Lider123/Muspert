package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.TrackInfo
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCase
import io.reactivex.Single

class GetFavoriteTrackInfosUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCase<List<TrackInfo>> {

    override fun execute(): Single<List<TrackInfo>> =
        catalogRepository.getFavoriteTrackInfos()
}
