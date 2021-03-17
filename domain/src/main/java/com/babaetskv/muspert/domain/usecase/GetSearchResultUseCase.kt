package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.GetSearchResultParams
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetSearchResultUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<GetSearchResultParams, List<Album>> {

    override fun execute(params: GetSearchResultParams): Single<List<Album>> =
        catalogRepository.getSearchResult(params)
}
