package com.babaetskv.muspert.utils

import io.reactivex.disposables.Disposable

fun Disposable.safeDispose() {
    if (!isDisposed) dispose()
}
