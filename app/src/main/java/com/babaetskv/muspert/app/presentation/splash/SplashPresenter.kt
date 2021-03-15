package com.babaetskv.muspert.app.presentation.splash

import com.babaetskv.muspert.app.NavGraphDirections
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.domain.model.TrackPushData
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.SplashFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.domain.usecase.GetProfileUseCase

class SplashPresenter(
    private val trackData: TrackPushData?,
    private val getProfileUseCase: GetProfileUseCase,
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
        } else {
            if (trackData != null) {
                navigator.replaceWith(
                    SplashFragmentDirections.actionSplashFragmentToMainFragment(),
                    NavGraphDirections.actionToPlayerFragment(trackData.collectionId, trackData.trackId)
                )
            } else {
                navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
            }
        }
    }

    private fun loadProfile() {
        getProfileUseCase.execute()
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
