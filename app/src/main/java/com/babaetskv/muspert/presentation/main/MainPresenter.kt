package com.babaetskv.muspert.presentation.main

import android.view.MenuItem
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class MainPresenter(
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<MainView>(errorHandler, notifier) {

    fun onPlaybackControlsClick() {
        val action = MainFragmentDirections.actionMainFragmentToPlayerFragment(PlaybackService.albumId, PlaybackService.trackId)
        navigator.forward(action)
    }

    fun onBottomNavigate(item: MenuItem) {
        viewState.openTab(item.itemId)
    }
}
