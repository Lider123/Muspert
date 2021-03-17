package com.babaetskv.muspert.app.presentation.main

import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainView : BaseView {

    @AddToEndSingle
    fun openTab(id: Int)
}
