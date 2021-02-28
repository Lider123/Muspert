package com.babaetskv.muspert.presentation.profile

import android.net.Uri
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.gateway.AuthGateway
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class ProfilePresenter(
    private val profileRepository: ProfileRepository,
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
        profileRepository.getProfile()
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
        profileRepository.updateAvatar(imageUri)
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
