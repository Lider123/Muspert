package com.babaetskv.muspert.app.presentation.home

import com.babaetskv.muspert.app.ErrorHandler
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.domain.model.GetAlbumsParams
import com.babaetskv.muspert.domain.model.GetGenresParams
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.GetAlbumsUseCase
import com.babaetskv.muspert.domain.usecase.GetGenresUseCase

class HomePresenter(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getGenresUseCase: GetGenresUseCase,
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
        getAlbumsUseCase.execute(params)
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
        getGenresUseCase.execute(params)
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
