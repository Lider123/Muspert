package com.babaetskv.muspert.presentation.tracks

import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.event.Event
import com.babaetskv.muspert.data.event.EventHub
import com.babaetskv.muspert.data.event.EventObserver
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.network.gateway.FavoritesGateway
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.TracksFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class TracksPresenter(
    private val album: Album,
    private val catalogRepository: CatalogRepository,
    private val favoritesGateway: FavoritesGateway,
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
        catalogRepository.getTracks(album.id)
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
        favoritesGateway.addToFavorites(track.id)
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
        favoritesGateway.removeFromFavorites(track.id)
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
