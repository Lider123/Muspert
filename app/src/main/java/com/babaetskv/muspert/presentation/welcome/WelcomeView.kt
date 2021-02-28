package com.babaetskv.muspert.presentation.welcome

import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEnd

interface WelcomeView : BaseView {

    @AddToEnd
    fun showNextPage()
}
