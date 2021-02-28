package com.babaetskv.muspert.presentation.main

import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainView : BaseView {

    @AddToEndSingle
    fun openTab(id: Int)
}
