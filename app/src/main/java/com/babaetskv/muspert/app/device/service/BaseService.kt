package com.babaetskv.muspert.app.device.service

import android.app.Service
import com.babaetskv.muspert.app.utils.safeDispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseService : Service() {
    private val disposable = CompositeDisposable()

    protected fun Disposable.unsubscribeOnDestroy() {
        disposable.add(this)
    }

    override fun onDestroy() {
        disposable.safeDispose()
        super.onDestroy()
    }
}
