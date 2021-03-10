package com.babaetskv.muspert.domain.usecase.base

import io.reactivex.Completable

interface CompletableUseCaseWithParams<P> {

    fun execute(params: P): Completable
}
