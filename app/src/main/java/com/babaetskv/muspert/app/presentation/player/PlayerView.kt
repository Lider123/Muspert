package com.babaetskv.muspert.app.presentation.player

import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface PlayerView : BaseView {

    @OneExecution
    fun startPlayer(albumId: Long, trackId: Long)

    @AddToEndSingle
    fun populateVolume(max: Int, current: Int)

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun showProgress()

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun hideProgress()

    @Skip
    fun updateIsFavorite(isFavorite: Boolean)

    companion object {
        private const val PROGRESS = "PROGRESS"
    }
}
