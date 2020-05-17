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
    private val schedulersProvider: SchedulersProvider by inject()

    fun setParams() {
        profileRepository.syncProfile()
            .observeOn(schedulersProvider.UI)
            .subscribe(::onSyncProfileSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onSyncProfileSuccess() {
        profileRepository.getProfile()
            .observeOn(schedulersProvider.UI)
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
