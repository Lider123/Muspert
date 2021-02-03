package com.babaetskv.muspert.presentation.profile

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.presentation.base.BaseView

interface ProfileView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateData(data: User)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()
}
