package com.babaetskv.muspert.presentation.profile

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.data.models.User

interface ProfileView : MvpView {

    @StateStrategyType(AddToEndStrategy::class)
    fun populateData(data: User)
}
