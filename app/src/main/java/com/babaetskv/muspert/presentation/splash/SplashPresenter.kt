package com.babaetskv.muspert.presentation.splash

import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.prefs.app.AppPrefs
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.SplashFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class SplashPresenter(
    private val profileRepository: ProfileRepository,
    private val schedulersProvider: SchedulersProvider,
    private val appPrefs: AppPrefs,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<SplashView>(errorHandler, notifier) {

    fun onDelay() {
        if (!appPrefs.welcomeShowed) {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToWelcomeFragment())
        } else if (!appPrefs.isAuthorized) {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        } else if (!appPrefs.profileFilled) {
            loadProfile()
        } else navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
    }

    private fun loadProfile() {
        profileRepository.getProfile()
            .observeOn(schedulersProvider.UI)
            .subscribe(::onGetProfileSuccess, ::onGetProfileError)
            .unsubscribeOnDestroy()
    }

    private fun onGetProfileSuccess(user: User) {
        appPrefs.profileFilled = user.isFilled
        if (user.isFilled) {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
        } else {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToSignUpFragment(user))
        }
    }

    private fun onGetProfileError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
        navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
    }
}
