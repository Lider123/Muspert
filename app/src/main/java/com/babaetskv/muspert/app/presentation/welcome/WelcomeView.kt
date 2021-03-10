package com.babaetskv.muspert.app.presentation.welcome

import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEnd

interface WelcomeView : BaseView {

    @AddToEnd
    fun showNextPage()
}
