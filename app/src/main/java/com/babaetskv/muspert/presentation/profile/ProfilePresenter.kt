package com.babaetskv.muspert.presentation.profile

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    private val user: User by inject()
    private val profileRepository: ProfileRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadProfile()
    }

    private fun loadProfile() {
        viewState.showProgress()
        profileRepository.getProfile()
            .observeOn(schedulersProvider.UI)
            .doFinally {
                viewState.hideProgress()
            }
            .subscribe(::onGetProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetProfileSuccess(user: User) {
        this.user.copy(user)
        viewState.populateData(user)
    }

    private fun onUploadAvatarSuccess() {
        loadProfile()
    }

    private fun onError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }

    fun onChangeAvatar(imageUri: Uri) {
        viewState.showProgress()
        profileRepository.updateAvatar(imageUri)
            .observeOn(schedulersProvider.UI)
            .doFinally {
                viewState.hideProgress()
            }
            .subscribe(::onUploadAvatarSuccess, ::onError)
            .unsubscribeOnDestroy()
    }
}
