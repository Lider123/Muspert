package com.babaetskv.muspert.presentation.main

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    fun onPlaybackControlsClick() {
        val action = MainFragmentDirections.actionMainFragmentToPlayerFragment(PlaybackService.albumId, PlaybackService.trackId)
        navigator.forward(action)
    }
}
