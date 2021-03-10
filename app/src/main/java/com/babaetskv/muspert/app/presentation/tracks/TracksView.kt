package com.babaetskv.muspert.app.presentation.tracks

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface TracksView : BaseView {

    @AddToEndSingle
    fun populateTracks(tracks: List<Track>)

    @AddToEndSingle
    fun populateAlbum(album: Album)

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()

    @AddToEndSingle
    fun showErrorView(show: Boolean)
}
