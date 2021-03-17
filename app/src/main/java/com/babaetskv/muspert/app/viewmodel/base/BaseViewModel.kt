package com.babaetskv.muspert.app.viewmodel.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

@Deprecated("Use pagination instead")
abstract class BaseViewModel : ViewModel() {
    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
