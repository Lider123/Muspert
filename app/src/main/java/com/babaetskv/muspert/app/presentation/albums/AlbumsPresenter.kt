package com.babaetskv.muspert.app.presentation.albums

import com.babaetskv.muspert.app.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.GetAlbumsParams
import com.babaetskv.muspert.app.device.service.PlaybackService
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.presentation.base.DefaultPaginator
import com.babaetskv.muspert.app.ui.fragments.AlbumsFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.GetAlbumsUseCase
import io.reactivex.Single
import moxy.InjectViewState

@InjectViewState
class AlbumsPresenter(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<AlbumsView>(errorHandler, notifier), DefaultPaginator.PaginatorCallback<Album> {
    private lateinit var paginator: DefaultPaginator<Album>

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

    override fun onNextLoaded(items: List<Album>) {
        viewState.showEmptyView(false)
        viewState.populateAlbums(items)
    }

    override fun onEmptyData() {
        viewState.showEmptyView(true)
    }

    override fun onPagingError(t: Throwable) {
        onError(t)
        viewState.showErrorView(true)
    }

    private fun loadNext(limit: Long, offset: Long): Single<List<Album>> {
        val params = GetAlbumsParams(
            limit = limit,
            offset = offset
        )
        return getAlbumsUseCase.execute(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
    }

    fun onSelectAlbum(album: Album) {
        val action = AlbumsFragmentDirections.actionAlbumsFragmentToTracksFragment(album)
        navigator.forward(action)
    }

    fun onPlaybackControlsClick() {
        val action = AlbumsFragmentDirections.actionAlbumsFragmentToPlayerFragment(
            albumId = PlaybackService.currAlbumId,
            trackId = PlaybackService.currTrackId
        )
        navigator.forward(action)
    }

    fun refreshAlbums() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNextPage()
    }

    companion object {
        private const val PAGE_SIZE = 6
    }
}
