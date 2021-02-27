package com.babaetskv.muspert.presentation.welcome

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.presentation.base.BaseView

interface WelcomeView : BaseView {

    @StateStrategyType(AddToEndStrategy::class)
    fun showNextPage()
}
