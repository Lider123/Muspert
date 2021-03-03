package com.babaetskv.muspert.presentation.favorites

import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FavoritesView : BaseView {

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun showProgress()

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun hideProgress()

    @AddToEndSingle
    fun showErrorView(show: Boolean)

    @AddToEndSingle
    fun showEmptyView(show: Boolean)

    @AddToEndSingle
    fun populateTracks(tracks: List<Track>)

    companion object {
        private const val PROGRESS = "PROGRESS"
        private const val DATA_UPDATE = "DATA_UPDATE"
    }
}
