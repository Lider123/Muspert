package com.babaetskv.muspert.app.presentation.cache

import com.babaetskv.muspert.app.device.service.PlaybackService
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.presentation.base.DefaultPaginator
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.domain.model.GetCacheTracksParams
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.usecase.GetCacheTracksUseCase
import io.reactivex.Single
import moxy.InjectViewState

@InjectViewState
class CachePresenter(
    private val getCacheTracksUseCase: GetCacheTracksUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<CacheView>(errorHandler, notifier),
    DefaultPaginator.PaginatorCallback<Track> {
    private lateinit var paginator: DefaultPaginator<Track>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        paginator = DefaultPaginator(PAGE_SIZE, ::loadNext, this).apply {
            refresh()
        }
    }
    override fun onDestroy() {
        if (::paginator.isInitialized) paginator.onDestroy()
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

    private fun loadNext(limit: Long, offset: Long): Single<List<Track>> {
        val params = GetCacheTracksParams(
            limit = limit,
            offset = offset
        )
        return getCacheTracksUseCase.execute(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
    }

    fun refreshCache() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNextPage()
    }

    fun onSelectTrack(track: Track) {
        val action = MainFragmentDirections.actionMainFragmentToPlayerFragment(PlaybackService.CACHE_ALBUM_ID, track.id)
        navigator.forward(action)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
