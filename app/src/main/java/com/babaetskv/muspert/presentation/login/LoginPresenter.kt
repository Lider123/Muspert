package com.babaetskv.muspert.presentation.login

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.gateway.AuthGateway
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.LoginFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {
    private val notifier: Notifier by inject()
    private val authGateway: AuthGateway by inject()
    private val profileRepository: ProfileRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val user: User by inject()
    private val errorHandler: ErrorHandler by inject()

    fun onLoginClick() {
        viewState.showAuthProgress()
        viewState.authPhone()
    }

    fun onLogin(token: String) {
        viewState.hideAuthProgress()
        authGateway.authorize(token)
            .andThen(profileRepository.getProfile())
            .observeOn(schedulersProvider.UI)
            .subscribe(::onGetProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    fun onAuthCancel() {
        viewState.hideAuthProgress()
    }

    fun onCodeEntered() {
        viewState.showVerificationProgress()
        viewState.verifySms()
    }

    fun onAuthError(message: String) {
        viewState.hideAuthProgress()
        viewState.hideVerificationProgress()
        notifier.sendMessage(message)
    }

    private fun onGetProfileSuccess(user: User) {
        this.user.copy(user)
        if (user.isRegistered) {
            navigator.replaceWith(LoginFragmentDirections.actionLoginFragmentToMainFragment())
        } else {
            navigator.forward(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
    }

    private fun onError(t: Throwable) {
        viewState.hideAuthProgress()
        viewState.hideVerificationProgress()
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }
}
