package com.babaetskv.muspert.app.presentation.welcome

import com.babaetskv.muspert.app.ErrorHandler
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.WelcomeFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier

class WelcomePresenter(
    private val appPrefs: AppPrefs,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<WelcomeView>(errorHandler, notifier) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appPrefs.welcomeShowed = true
    }

    fun onNext() {
        viewState.showNextPage()
    }

    fun finishWelcome() {
        navigator.replaceWith(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
    }
}
