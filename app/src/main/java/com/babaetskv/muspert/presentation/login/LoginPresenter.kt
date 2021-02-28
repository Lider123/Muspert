package com.babaetskv.muspert.presentation.login

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.gateway.AuthGateway
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.LoginFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class LoginPresenter(
    private val authGateway: AuthGateway,
    private val profileRepository: ProfileRepository,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<LoginView>(errorHandler, notifier) {

    override fun onError(t: Throwable) {
        super.onError(t)
        viewState.hideAuthProgress()
        viewState.hideVerificationProgress()
    }

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
        if (user.isRegistered) {
            navigator.replaceWith(LoginFragmentDirections.actionLoginFragmentToMainFragment())
        } else {
            navigator.forward(LoginFragmentDirections.actionLoginFragmentToSignUpFragment(user))
        }
    }
}
