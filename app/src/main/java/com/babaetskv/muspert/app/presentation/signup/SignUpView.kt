package com.babaetskv.muspert.app.presentation.signup

import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface SignUpView : BaseView {

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()
}
