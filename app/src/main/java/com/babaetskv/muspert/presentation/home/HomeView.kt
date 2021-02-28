package com.babaetskv.muspert.presentation.home

import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface HomeView : BaseView {

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()

    @AddToEndSingle
    fun populateAlbums(albums: List<Album>)

    @AddToEndSingle
    fun populateGenres(genres: List<Genre>)

    @AddToEndSingle
    fun showAlbumsErrorView(show: Boolean)

    @AddToEndSingle
    fun showGenresErrorView(show: Boolean)
}
