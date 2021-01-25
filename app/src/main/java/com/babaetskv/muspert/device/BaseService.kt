package com.babaetskv.muspert.device

import android.app.Service
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseService : Service() {
    private val disposable = CompositeDisposable()

    protected fun Disposable.unsubscribeOnDestroy() {
        disposable.add(this)
    }

    override fun onDestroy() {
        with (disposable) {
            if (!isDisposed) dispose()
        }
        super.onDestroy()
    }
}
