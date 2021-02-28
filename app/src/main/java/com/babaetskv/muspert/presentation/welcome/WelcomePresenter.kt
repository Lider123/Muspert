package com.babaetskv.muspert.presentation.welcome

import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.prefs.app.AppPrefs
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.WelcomeFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

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
