package com.babaetskv.muspert.app.presentation.albums

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.app.presentation.base.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface AlbumsView : BaseView {

    @AddToEndSingle
    fun showProgress()

    @AddToEndSingle
    fun hideProgress()

    @AddToEndSingle
    fun showErrorView(show: Boolean)

    @AddToEndSingle
    fun showEmptyView(show: Boolean)

    @AddToEndSingle
    fun populateAlbums(albums: List<Album>)
}
