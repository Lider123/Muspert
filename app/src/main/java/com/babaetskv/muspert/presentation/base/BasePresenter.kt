package com.babaetskv.muspert.presentation.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent

abstract class BasePresenter<T : MvpView> : MvpPresenter<T>(), KoinComponent {
    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
        super.onDestroy()
    }

    protected fun Disposable.unsubscribeOnDestroy() {
        compositeDisposable.add(this)
    }
}
