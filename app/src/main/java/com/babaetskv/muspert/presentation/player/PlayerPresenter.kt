package com.babaetskv.muspert.presentation.player

import android.media.AudioManager
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.event.Event
import com.babaetskv.muspert.data.event.EventHub
import com.babaetskv.muspert.data.event.EventObserver
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.network.gateway.FavoritesGateway
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.utils.notifier.Notifier

class PlayerPresenter(
    private val albumId: Long,
    private val trackId: Long,
    private val audioManager: AudioManager,
    private val eventHub: EventHub,
    private val favoritesGateway: FavoritesGateway,
    private val schedulersProvider: SchedulersProvider,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<PlayerView>(errorHandler, notifier), EventObserver {
    private var currTrack: Track? = null

    init {
        eventHub.subscribe(this, Event.FAVORITES_UPDATE)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (PlaybackService.checkCurrTrack(albumId, trackId).not()) {
            viewState.startPlayer(albumId, trackId)
        }
        updateViewVolume()
    }

    override fun onDestroy() {
        eventHub.unsubscribe(this)
        super.onDestroy()
    }

    override fun onNextEvent(event: Event, data: Any?) {
        when (event) {
            Event.FAVORITES_UPDATE -> {
                if (data is Track && data.id == currTrack?.id) viewState.updateIsFavorite(data.isFavorite)
            }
        }
    }

    private fun updateViewVolume() {
        viewState.populateVolume(
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        )
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

    private fun addToFavorites(track: Track) {
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

    private fun removeFromFavorites(track: Track) {
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

    fun setCurrentTrack(track: Track?) {
        currTrack = track
    }

    fun onVolumeChange(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        updateViewVolume()
    }

    fun onLike() {
        currTrack?.let {
            if (it.isFavorite) removeFromFavorites(it) else addToFavorites(it)
        }
    }
}
