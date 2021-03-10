package com.babaetskv.muspert.app.presentation.search

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface SearchView : BaseView {

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun showProgress()

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = PROGRESS)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = BANNER)
    fun showPresearchView(show: Boolean)

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = BANNER)
    fun showEmptyView(show: Boolean)

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = BANNER)
    fun showErrorView(show: Boolean)

    @AddToEndSingle
    fun populateAlbums(albums: List<Album>)

    @Skip
    fun populateQuery(query: String)

    companion object {
        private const val PROGRESS = "PROGRESS"
        private const val BANNER = "BANNER"
    }
}
