package com.babaetskv.muspert.presentation.signup

import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface SignUpView : BaseView {

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()
}
