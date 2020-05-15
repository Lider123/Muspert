package com.babaetskv.muspert.presentation.profile

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import org.koin.core.inject


@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    private val profileRepository: ProfileRepository by inject()
    private val user: User by inject()

    fun setParams() {
        profileRepository.syncProfile()
            .observeOn(SchedulersProvider.UI)
            .subscribe(::onSyncProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onSyncProfileSuccess() {
        profileRepository.getProfile()
            .observeOn(SchedulersProvider.UI)
            .subscribe(::onGetProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetProfileSuccess(user: User) {
        this.user.copy(user)
        viewState.populateData(user)
    }

    private fun onError(t: Throwable) {
        // TODO
    }
}
