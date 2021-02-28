package com.babaetskv.muspert.presentation.albums

import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.presentation.base.BaseView
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
