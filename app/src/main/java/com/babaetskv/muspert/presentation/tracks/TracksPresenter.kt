package com.babaetskv.muspert.presentation.tracks

import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.service.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.TracksFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier

class TracksPresenter(
    private val album: Album,
    private val catalogRepository: CatalogRepository,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<TracksView>(errorHandler, notifier) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.populateAlbum(album)
        loadTracks()
    }

    override fun onError(t: Throwable) {
        super.onError(t)
        viewState.showErrorView(true)
    }

    private fun onGetTracksSuccess(tracks: List<Track>) {
        viewState.showErrorView(false)
        viewState.populateTracks(tracks)
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
            albumId = PlaybackService.albumId,
            trackId = PlaybackService.trackId
        )
        navigator.forward(action)
    }
}
