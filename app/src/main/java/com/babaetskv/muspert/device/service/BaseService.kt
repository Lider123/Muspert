package com.babaetskv.muspert.device.service

import androidx.lifecycle.LifecycleService
import com.babaetskv.muspert.utils.safeDispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseService : LifecycleService() {
    private val disposable = CompositeDisposable()

    protected fun Disposable.unsubscribeOnDestroy() {
        disposable.add(this)
    }

    override fun onDestroy() {
        disposable.safeDispose()
        super.onDestroy()
    }
}
