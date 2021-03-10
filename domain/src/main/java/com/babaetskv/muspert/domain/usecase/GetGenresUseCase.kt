package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.domain.model.GetGenresParams
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class GetGenresUseCase(
    private val catalogRepository: CatalogRepository
) : SingleUseCaseWithParams<GetGenresParams, List<Genre>> {

    override fun execute(params: GetGenresParams): Single<List<Genre>> =
        catalogRepository.getGenres(params)
}
