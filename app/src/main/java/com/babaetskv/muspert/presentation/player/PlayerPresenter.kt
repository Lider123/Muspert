package com.babaetskv.muspert.presentation.player

import android.media.AudioManager
import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter

@InjectViewState
class PlayerPresenter(
    private val albumId: Long,
    private val trackId: Long,
    private val audioManager: AudioManager
) : BasePresenter<PlayerView>() {

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
