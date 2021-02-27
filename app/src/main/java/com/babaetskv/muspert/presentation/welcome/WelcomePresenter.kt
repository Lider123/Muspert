package com.babaetskv.muspert.presentation.welcome

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.prefs.app.AppPrefs
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.WelcomeFragmentDirections
import org.koin.core.inject

@InjectViewState
class WelcomePresenter : BasePresenter<WelcomeView>() {
    private val appPrefs: AppPrefs by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // appPrefs.welcomeShowed = true TODO: uncomment
    }

    fun onNext() {
        viewState.showNextPage()
    }

    fun finishWelcome() {
        navigator.replaceWith(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
    }
}
