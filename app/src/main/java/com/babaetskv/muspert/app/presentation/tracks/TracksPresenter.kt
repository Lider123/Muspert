package com.babaetskv.muspert.app.presentation.tracks

import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.app.event.Event
import com.babaetskv.muspert.app.event.EventHub
import com.babaetskv.muspert.app.event.EventObserver
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.app.device.service.PlaybackService
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.ui.fragments.TracksFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.domain.usecase.AddToFavoritesUseCase
import com.babaetskv.muspert.domain.usecase.GetAlbumTracksUseCase
import com.babaetskv.muspert.domain.usecase.RemoveFromFavoritesUseCase

class TracksPresenter(
    private val album: Album,
    private val getAlbumTracksUseCase: GetAlbumTracksUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val eventHub: EventHub,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<TracksView>(errorHandler, notifier), EventObserver {

    init {
        eventHub.subscribe(this, Event.FAVORITES_UPDATE)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.populateAlbum(album)
        loadTracks()
    }

    override fun onDestroy() {
        eventHub.unsubscribe(this)
        super.onDestroy()
    }

    override fun onError(t: Throwable) {
        super.onError(t)
        viewState.showErrorView(true)
    }

    override fun onNextEvent(event: Event, data: Any?) {
        when (event) {
            Event.FAVORITES_UPDATE -> loadTracks()
        }
    }

    private fun onGetTracksSuccess(tracks: List<Track>) {
        viewState.showErrorView(false)
        viewState.populateTracks(tracks)
    }

    private fun onAddToFavoritesSuccess(track: Track) {
        notifier.sendMessage(R.string.added_to_favorites_message)
        track.isFavorite = !track.isFavorite
        eventHub.sendEvent(Event.FAVORITES_UPDATE, track)
    }

    private fun onRemoveFromFavoritesSuccess(track: Track) {
        notifier.sendMessage(R.string.removed_from_favorites_message)
        track.isFavorite = !track.isFavorite
        eventHub.sendEvent(Event.FAVORITES_UPDATE, track)
    }

    fun loadTracks() {
        getAlbumTracksUseCase.execute(album.id)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe(::onGetTracksSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    fun onSelectTrack(track: Track) {
        val action = TracksFragmentDirections.actionTracksFragmentToPlayerFragment(
            albumId = track.albumId,
            trackId = track.id
        )
        navigator.forward(action)
    }

    fun onPlaybackControlsClick() {
        val action = TracksFragmentDirections.actionTracksFragmentToPlayerFragment(
            albumId = PlaybackService.currAlbumId,
            trackId = PlaybackService.currTrackId
        )
        navigator.forward(action)
    }

    fun addToFavorites(track: Track) {
        addToFavoritesUseCase.execute(track.id)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe({ onAddToFavoritesSuccess(track) }, ::onError)
            .unsubscribeOnDestroy()
    }

    fun removeFromFavorites(track: Track) {
        removeFromFavoritesUseCase.execute(track.id)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
            .subscribe({ onRemoveFromFavoritesSuccess(track) }, ::onError)
            .unsubscribeOnDestroy()
    }
}
