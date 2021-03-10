package com.babaetskv.muspert.app.presentation.main

import android.view.MenuItem
import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.app.device.service.PlaybackService
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier

class MainPresenter(
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<MainView>(errorHandler, notifier) {

    fun onPlaybackControlsClick() {
        val action = MainFragmentDirections.actionMainFragmentToPlayerFragment(
            albumId = PlaybackService.currAlbumId,
            trackId = PlaybackService.currTrackId
        )
        navigator.forward(action)
    }

    fun onBottomNavigate(item: MenuItem) {
        viewState.openTab(item.itemId)
    }
}
