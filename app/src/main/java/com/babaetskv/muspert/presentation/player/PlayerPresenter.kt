package com.babaetskv.muspert.presentation.player

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter

@InjectViewState
class PlayerPresenter : BasePresenter<PlayerView>() {

    fun onStart(albumId: Long, trackId: Long) {
        if (PlaybackService.checkCurrTrack(albumId, trackId).not()) {
            viewState.startPlayer(albumId, trackId)
        }
    }
}
