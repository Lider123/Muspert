package com.babaetskv.muspert.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.arellomobile.mvp.MvpAppCompatFragment
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

/**
 * @author Konstantin on 13.05.2020
 */
abstract class BaseFragment : MvpAppCompatFragment() {
    private val schedulersProvider: SchedulersProvider by inject()
    protected val navigator: AppNavigator by inject()

    private var playbackDisposable: Disposable? = null

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
        playbackDisposable = PlaybackService.updateViewSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextPlaybackCommand)
    }

    private fun unsubscribeFromPlaybackService() {
        playbackDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        playbackControls?.run {
            if (data.track == null) hide() else {
                setTitle(data.track.title)
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
                setCover(R.drawable.logo_white)
                // TODO: do smth with cover and progress
                show()
            }
        }
    }

    protected open fun onBackPressed() = requireActivity().onBackPressed()
}
