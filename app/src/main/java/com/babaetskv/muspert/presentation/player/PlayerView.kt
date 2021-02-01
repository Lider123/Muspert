package com.babaetskv.muspert.presentation.player

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface PlayerView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun startPlayer(albumId: Long, trackId: Long)
}
