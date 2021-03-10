package com.babaetskv.muspert.app.presentation.base

import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.app.utils.safeDispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter

abstract class BasePresenter<T : BaseView>(
    protected val errorHandler: ErrorHandler,
    protected val notifier: Notifier
) : MvpPresenter<T>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.safeDispose()
        super.onDestroy()
    }

    protected fun Disposable.unsubscribeOnDestroy() {
        compositeDisposable.add(this)
    }

    protected open fun onError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }
}
