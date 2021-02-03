package com.babaetskv.muspert.presentation.player

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.presentation.base.BaseView

interface PlayerView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun startPlayer(albumId: Long, trackId: Long)
}
