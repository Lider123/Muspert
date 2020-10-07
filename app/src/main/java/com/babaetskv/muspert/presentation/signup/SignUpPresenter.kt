package com.babaetskv.muspert.presentation.signup

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.SignUpFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class SignUpPresenter : BasePresenter<SignUpView>() {
    private val profile: User by inject()
    private val profileRepository: ProfileRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()

    var firstName = ""
    var lastName = ""
    var nickname = ""
    val dataFilled: Boolean
        get() = firstName.isNotEmpty() && lastName.isNotEmpty() && nickname.isNotEmpty()

    private fun onUpdateProfileSuccess() {
        navigator.newStack(SignUpFragmentDirections.actionSignUpFragmentToMainFragment())
    }

    private fun onError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }

    fun onConfirm() {
        profile.apply {
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
