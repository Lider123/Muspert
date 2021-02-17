package com.babaetskv.muspert.presentation.main

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.presentation.base.BaseView

interface MainView : BaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun openTab(id: Int)
}
