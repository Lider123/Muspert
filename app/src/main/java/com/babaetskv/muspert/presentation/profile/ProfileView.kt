package com.babaetskv.muspert.presentation.profile

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.data.models.User

interface ProfileView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateData(data: User)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()
}
