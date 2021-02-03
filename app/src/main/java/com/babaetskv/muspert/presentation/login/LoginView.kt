package com.babaetskv.muspert.presentation.login

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.presentation.base.BaseView

interface LoginView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAuthProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideAuthProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showVerificationProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideVerificationProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setMode(mode: Mode)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun authPhone()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun verifySms()

    enum class Mode {
        LOGIN, SMS
    }
}
