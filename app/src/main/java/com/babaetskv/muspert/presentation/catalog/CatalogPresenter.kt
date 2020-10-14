package com.babaetskv.muspert.presentation.catalog

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.data.models.GetAlbumsParams
import com.babaetskv.muspert.data.models.GetGenresParams
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class CatalogPresenter : BasePresenter<CatalogView>() {
    private val catalogRepository: CatalogRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val notifier: Notifier by inject()
    private val errorHandler: ErrorHandler by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadAlbums()
        loadGenres()
    }

    private fun onGetAlbumsSuccess(albums: List<Album>) {
        viewState.showAlbumsErrorView(false)
        viewState.populateAlbums(albums)
    }

    private fun onGetAlbumsError(t: Throwable) {
        viewState.showAlbumsErrorView(true)
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }

    private fun onGetGenresSuccess(genres: List<Genre>) {
        viewState.showGenresErrorView(false)
        viewState.populateGenres(genres)
    }

    private fun onGetGenresError(t: Throwable) {
        viewState.showGenresErrorView(true)
        errorHandler.handle(t) { notifier.sendMessage(it) }
    }

    fun loadAlbums() {
        val params = GetAlbumsParams(
            limit = ALBUMS_LIMIT,
            offset = 0
        )
        catalogRepository.getAlbums(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onGetAlbumsSuccess, ::onGetAlbumsError)
            .unsubscribeOnDestroy()
    }

    fun loadGenres() {
        val params = GetGenresParams(
            limit = GENRES_LIMIT,
            offset = 0
        )
        catalogRepository.getGenres(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onGetGenresSuccess, ::onGetGenresError)
            .unsubscribeOnDestroy()
    }
    
    fun onSelectAlbum(album: Album) {
        // TODO: go to the album page
        notifier.sendMessage(R.string.in_development)
    }

    fun onAlbumsClick() {
        val actions = MainFragmentDirections.actionMainFragmentToAlbumsFragment()
        navigator.forward(actions)
    }

    fun onSelectGenre(genre: Genre) {
        // TODO: go to the genre page
        notifier.sendMessage(R.string.in_development)
    }

    fun onGenresClick() {
        // TODO: go to genres list page
        notifier.sendMessage(R.string.in_development)
    }

    companion object {
        private const val ALBUMS_LIMIT = 5L
        private const val GENRES_LIMIT = 5L
    }
}
