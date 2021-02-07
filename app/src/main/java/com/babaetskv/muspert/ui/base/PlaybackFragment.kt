package com.babaetskv.muspert.ui.base

import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.utils.into
import com.babaetskv.muspert.utils.safeDispose
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

abstract class PlaybackFragment : BaseFragment() {
    private val schedulersProvider: SchedulersProvider by inject()

    private var playbackDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null

    protected abstract val playbackControls: PlaybackControls

    override fun onStart() {
        super.onStart()
        subscribeOnPlaybackService()
    }

    override fun onStop() {
        unsubscribeFromPlaybackService()
        super.onStop()
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
        playbackControls.run {
            setDuration(data.duration.div(1000).toInt())
            setProgress(data.progress.div(1000).toInt())
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        playbackControls.run {
            if (data.track == null) {
                hide()
                return
            }

            setTitle(data.track.artistName, data.track.title)
            setIsPlaying(data.isPlaying)
            setPlayCallback {
                PlaybackService.sendAction(requireContext(), PlaybackService.Action.Play)
            }
            setPrevCallback {
                PlaybackService.sendAction(requireContext(), PlaybackService.Action.Prev)
            }
            setNextCallback {
                PlaybackService.sendAction(requireContext(), PlaybackService.Action.Next)
            }
            setProgressListener(object : PlaybackControls.ProgressListener {

                override fun onChangeStart(): Boolean =
                    if (PlaybackService.isPlaying) {
                        PlaybackService.sendAction(requireContext(), PlaybackService.Action.Play)
                        true
                    } else false

                override fun onChangeEnd(resumePlayback: Boolean) {
                    if (resumePlayback) {
                        PlaybackService.sendAction(requireContext(), PlaybackService.Action.Play)
                    }
                }

                override fun onProgressChanged(progress: Int, duration: Int) {
                    val percentage = progress.toFloat().div(duration.toFloat())
                    PlaybackService.sendAction(requireContext(), PlaybackService.Action.Progress(percentage))
                }
            })
            Picasso.with(requireContext())
                .load(BuildConfig.API_URL + data.track.cover)
                .resize(0, 200)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(this)
            show()
        }
    }
}
