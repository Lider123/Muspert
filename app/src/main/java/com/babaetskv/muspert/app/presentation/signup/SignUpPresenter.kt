package com.babaetskv.muspert.app.presentation.signup

import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.SignUpFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.UpdateProfileUseCase

class SignUpPresenter(
    private val user: User,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    private val appPrefs: AppPrefs,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<SignUpView>(errorHandler, notifier) {
    var firstName = ""
    var lastName = ""
    var nickname = ""
    val dataFilled: Boolean
        get() = firstName.isNotEmpty() && lastName.isNotEmpty() && nickname.isNotEmpty()

    private fun onUpdateProfileSuccess() {
        appPrefs.profileFilled = true
        navigator.newStack(SignUpFragmentDirections.actionSignUpFragmentToMainFragment())
    }

    fun onConfirm() {
        user.apply {
            firstName = this@SignUpPresenter.firstName
            lastName = this@SignUpPresenter.lastName
            nickname = this@SignUpPresenter.nickname
        }.let {
            updateProfileUseCase.execute(it)
        }
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onUpdateProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }
}
