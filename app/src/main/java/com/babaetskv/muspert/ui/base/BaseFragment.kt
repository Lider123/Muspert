package com.babaetskv.muspert.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.arellomobile.mvp.MvpAppCompatFragment
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.utils.into
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

/**
 * @author Konstantin on 13.05.2020
 */
abstract class BaseFragment : MvpAppCompatFragment() {
    private val schedulersProvider: SchedulersProvider by inject()
    protected val navigator: AppNavigator by inject()

    private var playbackDisposable: Disposable? = null
    private var progressDisposable: Disposable? = null

    protected abstract val playbackControls: PlaybackControls?
    @get:LayoutRes
    protected abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onStart() {
        super.onStart()
        if (playbackControls != null) subscribeOnPlaybackService()
    }

    override fun onStop() {
        if (playbackControls != null) unsubscribeFromPlaybackService()
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
        playbackDisposable?.run {
            if (!isDisposed) dispose()
        }
        progressDisposable?.run {
            if (!isDisposed) dispose()
        }
    }

    private fun onNextProgressUpdate(data: ProgressData) {
        playbackControls?.run {
            setDuration(data.duration.div(1000).toInt())
            setProgress(data.progress.div(1000).toInt())
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        playbackControls?.run {
            if (data.track == null) hide() else {
                setTitle(getString(R.string.track_with_artist_placeholder, data.track.artistName, data.track.title))
                setIsPlaying(data.isPlaying)
                setPlayCallback {
                    PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_PLAY)
                }
                setPrevCallback {
                    PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_PREV)
                }
                setNextCallback {
                    PlaybackService.sendAction(requireContext(), PlaybackService.ACTION_NEXT)
                }
                Picasso.with(requireContext())
                    .load(BuildConfig.API_URL + data.track.cover)
                    .resize(0, 200)
                    .placeholder(R.drawable.logo_white)
                    .error(R.drawable.logo_white)
                    .into(this)
                show()
            }
        }
    }

    protected open fun onBackPressed() = requireActivity().onBackPressed()
}
