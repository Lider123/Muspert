package com.babaetskv.muspert.app.utils

import io.reactivex.disposables.Disposable

fun Disposable.safeDispose() {
    if (!isDisposed) dispose()
}
