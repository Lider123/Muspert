package com.babaetskv.muspert.presentation.profile

import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : BaseView {

    @AddToEndSingle
    fun populateData(data: User)

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()
}
