package com.babaetskv.muspert.presentation.base

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.babaetskv.muspert.navigation.AppNavigator
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BasePresenter<T : BaseView> : MvpPresenter<T>(), KoinComponent {
    protected val navigator: AppNavigator by inject()

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) compositeDisposable.dispose()
        super.onDestroy()
    }

    protected fun Disposable.unsubscribeOnDestroy() {
        compositeDisposable.add(this)
    }
}
