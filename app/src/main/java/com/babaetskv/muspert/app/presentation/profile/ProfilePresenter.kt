package com.babaetskv.muspert.app.presentation.profile

import android.net.Uri
import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.gateway.AuthGateway
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.GetProfileUseCase
import com.babaetskv.muspert.domain.usecase.UpdateAvatarUseCase

class ProfilePresenter(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val authGateway: AuthGateway,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<ProfileView>(errorHandler, notifier) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadProfile()
    }

    private fun loadProfile() {
        getProfileUseCase.execute()
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onGetProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetProfileSuccess(user: User) {
        viewState.populateData(user)
    }

    private fun onUploadAvatarSuccess() {
        loadProfile()
    }

    private fun onLogoutSuccess() {
        navigator.newStack(MainFragmentDirections.actionMainFragmentToLoginFragment())
    }

    fun onChangeAvatar(imageUri: Uri) {
        updateAvatarUseCase.execute(imageUri)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onUploadAvatarSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    fun onLogout() {
        authGateway.logout()
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onLogoutSuccess, ::onError)
            .unsubscribeOnDestroy()
    }
}
