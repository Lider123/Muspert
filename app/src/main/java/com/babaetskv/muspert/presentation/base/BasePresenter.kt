package com.babaetskv.muspert.presentation.base

import com.arellomobile.mvp.MvpPresenter
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.utils.safeDispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BasePresenter<T : BaseView> : MvpPresenter<T>(), KoinComponent {
    protected val navigator: AppNavigator by inject()

    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.safeDispose()
        super.onDestroy()
    }

    protected fun Disposable.unsubscribeOnDestroy() {
        compositeDisposable.add(this)
    }
}
