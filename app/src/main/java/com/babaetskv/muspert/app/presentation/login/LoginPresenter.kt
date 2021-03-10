package com.babaetskv.muspert.app.presentation.login

import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.LoginFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.AuthorizeUseCase

class LoginPresenter(
    private val authorizeUseCase: AuthorizeUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    private val appPrefs: AppPrefs,
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
        authorizeUseCase.execute(token)
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
        appPrefs.profileFilled = user.isFilled
        if (user.isFilled) {
            navigator.replaceWith(LoginFragmentDirections.actionLoginFragmentToMainFragment())
        } else {
            navigator.forward(LoginFragmentDirections.actionLoginFragmentToSignUpFragment(user))
        }
    }
}
