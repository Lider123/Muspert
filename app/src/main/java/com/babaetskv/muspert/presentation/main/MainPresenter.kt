package com.babaetskv.muspert.presentation.main

import android.view.MenuItem
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

    fun onBottomNavigate(item: MenuItem) {
        viewState.openTab(item.itemId)
    }
}
