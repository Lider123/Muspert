package com.babaetskv.muspert.app.presentation.profile

import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : BaseView {

    @AddToEndSingle
    fun populateData(data: User)

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()
}
