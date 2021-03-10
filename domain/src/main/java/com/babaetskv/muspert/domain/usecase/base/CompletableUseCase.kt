package com.babaetskv.muspert.domain.usecase.base

import io.reactivex.Completable

interface CompletableUseCase {

    fun execute(): Completable
}
