package com.babaetskv.muspert.presentation.player

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter

@InjectViewState
class PlayerPresenter(
    private val albumId: Long,
    private val trackId: Long
) : BasePresenter<PlayerView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (PlaybackService.checkCurrTrack(albumId, trackId).not()) {
            viewState.startPlayer(albumId, trackId)
        }
    }
}
