package com.babaetskv.muspert.app.presentation.favorites

import com.babaetskv.muspert.app.ErrorHandler
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.app.event.Event
import com.babaetskv.muspert.app.event.EventHub
import com.babaetskv.muspert.app.event.EventObserver
import com.babaetskv.muspert.domain.model.GetFavoriteTracksParams
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.app.device.service.PlaybackService.Companion.FAVORITES_ALBUM_ID
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.presentation.base.DefaultPaginator
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.AddToFavoritesUseCase
import com.babaetskv.muspert.domain.usecase.GetFavoriteTracksUseCase
import com.babaetskv.muspert.domain.usecase.RemoveFromFavoritesUseCase
import io.reactivex.Single

class FavoritesPresenter(
    private val getFavoriteTracksUseCase: GetFavoriteTracksUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
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
        if (::paginator.isInitialized) paginator.onDestroy()
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
        return getFavoriteTracksUseCase.execute(params)
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

    companion object {
        private const val PAGE_SIZE = 10
    }
}
