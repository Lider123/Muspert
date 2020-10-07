package com.babaetskv.muspert.presentation.splash

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.prefs.PrefsHelper
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.SplashFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {
    private val prefs: PrefsHelper by inject()
    private val profileRepository: ProfileRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val user: User by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()

    fun onDelay() {
        if (prefs.authTokenPreference.isSet) {
            profileRepository.getProfile()
                .observeOn(schedulersProvider.UI)
                .subscribe(::onGetProfileSuccess, ::onGetProfileError)
                .unsubscribeOnDestroy()
        } else navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
    }

    private fun onGetProfileSuccess(user: User) {
        this.user.copy(user)
        if (user.isRegistered) {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
        } else {
            navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToSignUpFragment())
        }
    }

    private fun onGetProfileError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
        navigator.replaceWith(SplashFragmentDirections.actionSplashFragmentToMainFragment())
    }
}
