package com.babaetskv.muspert.domain.usecase.base

import io.reactivex.Single

interface SingleUseCase<R> {

    fun execute(): Single<R>
}
