package com.babaetskv.muspert.presentation.tracks

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.ui.fragments.TracksFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.core.inject

@InjectViewState
class TracksPresenter(
    private val album: Album
) : BasePresenter<TracksView>() {
    private val catalogRepository: CatalogRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.populateAlbum(album)
        loadTracks()
    }

    private fun onGetTracksSuccess(tracks: List<Track>) {
        viewState.showErrorView(false)
        viewState.populateTracks(tracks)
    }

    private fun onError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
        viewState.showErrorView(true)
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
