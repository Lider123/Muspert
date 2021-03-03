package com.babaetskv.muspert.presentation.favorites

import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.event.Event
import com.babaetskv.muspert.data.event.EventHub
import com.babaetskv.muspert.data.event.EventObserver
import com.babaetskv.muspert.data.models.GetFavoriteTracksParams
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.network.gateway.FavoritesGateway
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.service.PlaybackService.Companion.FAVORITES_ALBUM_ID
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.presentation.base.DefaultPaginator
import com.babaetskv.muspert.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import io.reactivex.Single

class FavoritesPresenter(
    private val catalogRepository: CatalogRepository,
    private val favoritesGateway: FavoritesGateway,
    private val eventHub: EventHub,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<FavoritesView>(errorHandler, notifier),
    DefaultPaginator.PaginatorCallback<Track>,
    EventObserver {
    private lateinit var paginator: DefaultPaginator<Track>

    init {
        eventHub.subscribe(this, Event.FAVORITES_UPDATE)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        paginator = DefaultPaginator(PAGE_SIZE, ::loadNext, this).apply {
            refresh()
        }
    }

    override fun onDestroy() {
        paginator.onDestroy()
        eventHub.unsubscribe(this)
        super.onDestroy()
    }

    override fun onNextLoaded(items: List<Track>) {
        viewState.showEmptyView(false)
        viewState.populateTracks(items)
    }

    override fun onEmptyData() {
        viewState.showEmptyView(true)
    }

    override fun onPagingError(t: Throwable) {
        onError(t)
        viewState.showErrorView(true)
    }

    override fun onNextEvent(event: Event, data: Any?) {
        when (event) {
            Event.FAVORITES_UPDATE -> refreshFavorites()
        }
    }

    private fun loadNext(limit: Long, offset: Long): Single<List<Track>> {
        val params = GetFavoriteTracksParams(
            limit = limit,
            offset = offset
        )
        return catalogRepository.getFavoriteTracks(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
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

    fun refreshFavorites() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNextPage()
    }

    fun onSelectTrack(track: Track) {
        val action = MainFragmentDirections.actionMainFragmentToPlayerFragment(FAVORITES_ALBUM_ID, track.id)
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

    companion object {
        private const val PAGE_SIZE = 10
    }
}
