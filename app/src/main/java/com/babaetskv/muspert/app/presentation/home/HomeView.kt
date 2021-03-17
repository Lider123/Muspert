package com.babaetskv.muspert.app.presentation.home

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.app.presentation.base.BaseView
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
