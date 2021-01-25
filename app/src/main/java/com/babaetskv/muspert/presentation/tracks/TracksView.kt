package com.babaetskv.muspert.presentation.tracks

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track

interface TracksView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateTracks(tracks: List<Track>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun populateAlbum(album: Album)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showErrorView(show: Boolean)
}
