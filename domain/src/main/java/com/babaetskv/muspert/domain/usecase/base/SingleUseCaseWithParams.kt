package com.babaetskv.muspert.domain.usecase.base

import io.reactivex.Single

interface SingleUseCaseWithParams<P, R> {

    fun execute(params: P): Single<R>
}
