package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.GetAlbumsParams
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetAlbumsUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<GetAlbumsParams, List<Album>> {

    override fun execute(params: GetAlbumsParams): Single<List<Album>> =
        catalogRepository.getAlbums(params)
}
