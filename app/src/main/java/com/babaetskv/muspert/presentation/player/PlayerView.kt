package com.babaetskv.muspert.presentation.player

import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface PlayerView : BaseView {

    @OneExecution
    fun startPlayer(albumId: Long, trackId: Long)

    @AddToEndSingle
    fun populateVolume(max: Int, current: Int)
}
