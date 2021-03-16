package com.babaetskv.muspert.ui.base

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.utils.into
import com.babaetskv.muspert.utils.safeDispose
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable

class PlaybackObserver private constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val schedulersProvider: SchedulersProvider,
    private var playbackCallback: ((data: PlaybackData) -> Unit)? = null,
    private var progressCallback: ((data: ProgressData) -> Unit)? = null,
    private val playbackControls: PlaybackControls? = null
) : LifecycleObserver {
    private var playbackDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private fun subscribeOnPlaybackService() {
        playbackDisposable = PlaybackService.setTrackSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextPlaybackCommand)
        progressDisposable = PlaybackService.setProgressSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextProgressUpdate)
    }

    private fun unsubscribeFromPlaybackService() {
        playbackDisposable?.safeDispose()
        progressDisposable?.safeDispose()
    }

    private fun onNextProgressUpdate(data: ProgressData) {
        progressCallback?.invoke(data)
        playbackControls?.run {
            setDuration(data.duration.div(1000).toInt())
            setProgress(data.progress.div(1000).toInt())
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        playbackCallback?.invoke(data)
        playbackControls?.run {
            if (data.track == null) {
                hide()
                return
            }

            setTitle(data.track.artistName, data.track.title)
            setIsPlaying(data.isPlaying)
            setShuffleEnabled(data.shuffleEnabled)
            setRepeatEnabled(data.repeatEnabled)
            setPlayCallback {
                PlaybackService.sendAction(playbackContext, PlaybackService.Action.Play)
            }
            setPrevCallback {
                PlaybackService.sendAction(playbackContext, PlaybackService.Action.Prev)
            }
            setNextCallback {
                PlaybackService.sendAction(playbackContext, PlaybackService.Action.Next)
            }
            setShuffleCallback {
                PlaybackService.sendAction(playbackContext, PlaybackService.Action.Shuffle)
            }
            setRepeatCallback {
                PlaybackService.sendAction(playbackContext, PlaybackService.Action.Repeat)
            }
            setProgressListener(object : PlaybackControls.ProgressListener {

                override fun onChangeStart(): Boolean =
                    if (PlaybackService.isPlaying) {
                        PlaybackService.sendAction(playbackContext, PlaybackService.Action.Play)
                        true
                    } else false

                override fun onChangeEnd(resumePlayback: Boolean) {
                    if (resumePlayback) {
                        PlaybackService.sendAction(playbackContext, PlaybackService.Action.Play)
                    }
                }

                override fun onProgressChanged(progress: Int, duration: Int) {
                    val percentage = progress.toFloat().div(duration.toFloat())
                    PlaybackService.sendAction(playbackContext, PlaybackService.Action.Progress(percentage))
                }
            })
            Picasso.with(playbackContext)
                .load(BuildConfig.API_URL + data.track.cover)
                .resize(0, 200)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(this)
            show()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        if (lifecycleOwner is Service) subscribeOnPlaybackService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if (lifecycleOwner !is Service) subscribeOnPlaybackService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        if (lifecycleOwner !is Service) unsubscribeFromPlaybackService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (lifecycleOwner is Service) unsubscribeFromPlaybackService()
    }

    class Builder(
        private val lifecycleOwner: LifecycleOwner,
        private val schedulersProvider: SchedulersProvider
    ) {
        private var playbackCallback: ((data: PlaybackData) -> Unit)? = null
        private var progressCallback: ((data: ProgressData) -> Unit)? = null
        private var playbackControls: PlaybackControls? = null

        fun setPlaybackControls(playbackControls: PlaybackControls): Builder = apply {
            this.playbackControls = playbackControls
        }

        fun setPlaybackCallback(callback: (PlaybackData) -> Unit): Builder = apply {
            playbackCallback = callback
        }

        fun setProgressCallback(callback: (ProgressData) -> Unit): Builder = apply {
            progressCallback = callback
        }

        fun build(): PlaybackObserver =
            PlaybackObserver(
                lifecycleOwner,
                schedulersProvider,
                playbackCallback,
                progressCallback,
                playbackControls
            )
    }
}
