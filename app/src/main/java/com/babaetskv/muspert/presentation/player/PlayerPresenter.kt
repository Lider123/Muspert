package com.babaetskv.muspert.presentation.player

import android.media.AudioManager
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.utils.notifier.Notifier

class PlayerPresenter(
    private val albumId: Long,
    private val trackId: Long,
    private val audioManager: AudioManager,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<PlayerView>(errorHandler, notifier) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (PlaybackService.checkCurrTrack(albumId, trackId).not()) {
            viewState.startPlayer(albumId, trackId)
        }
        updateViewVolume()
    }

    private fun updateViewVolume() {
        viewState.populateVolume(
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        )
    }

    fun onVolumeChange(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        updateViewVolume()
    }
}
