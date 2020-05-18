package com.babaetskv.muspert.presentation.profile

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.presentation.base.BasePresenter
import org.koin.core.inject


@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    private val user: User by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.e("TEST", "Profile: $user")
        viewState.populateData(user)
    }
}
