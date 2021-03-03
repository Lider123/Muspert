package com.babaetskv.muspert.presentation.tracks

import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

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
