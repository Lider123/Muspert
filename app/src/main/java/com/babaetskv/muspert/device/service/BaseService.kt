package com.babaetskv.muspert.device.service

import androidx.lifecycle.LifecycleService
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.ui.base.PlaybackObserver
import com.babaetskv.muspert.utils.safeDispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

abstract class BaseService : LifecycleService() {
    protected val schedulersProvider: SchedulersProvider by inject()

    private val disposable = CompositeDisposable()
    private var playbackObserver: PlaybackObserver? = null
    protected open val playbackObserverInitializer: ((SchedulersProvider) -> PlaybackObserver)? = null

    protected fun Disposable.unsubscribeOnDestroy() {
        disposable.add(this)
    }

    override fun onCreate() {
        super.onCreate()
        playbackObserver = playbackObserverInitializer?.invoke(schedulersProvider)
    }

    override fun onDestroy() {
        disposable.safeDispose()
        super.onDestroy()
    }
}
