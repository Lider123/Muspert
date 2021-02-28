package com.babaetskv.muspert.presentation.home

import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.data.models.GetAlbumsParams
import com.babaetskv.muspert.data.models.GetGenresParams
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class HomePresenter(
    private val catalogRepository: CatalogRepository,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<HomeView>(errorHandler, notifier) {

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
        val action = MainFragmentDirections.actionMainFragmentToTracksFragment(album)
        navigator.forward(action)
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
