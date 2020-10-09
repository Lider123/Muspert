package com.babaetskv.muspert.presentation.catalog

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre

interface CatalogView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateAlbums(albums: List<Album>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateGenres(genres: List<Genre>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showAlbumsErrorView(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGenresErrorView(show: Boolean)
}
