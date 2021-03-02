package com.babaetskv.muspert.presentation.signup

import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.prefs.app.AppPrefs
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.SignUpFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class SignUpPresenter(
    private val user: User,
    private val profileRepository: ProfileRepository,
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
            profileRepository.updateProfile(it)
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
