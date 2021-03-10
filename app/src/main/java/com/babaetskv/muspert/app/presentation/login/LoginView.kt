package com.babaetskv.muspert.app.presentation.login

import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface LoginView : BaseView {

    @AddToEndSingle
    fun showAuthProgress()

    @AddToEndSingle
    fun hideAuthProgress()

    @AddToEndSingle
    fun showVerificationProgress()

    @AddToEndSingle
    fun hideVerificationProgress()

    @AddToEndSingle
    fun setMode(mode: Mode)

    @OneExecution
    fun authPhone()

    @OneExecution
    fun verifySms()

    enum class Mode {
        LOGIN, SMS
    }
}
