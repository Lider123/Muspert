package com.babaetskv.muspert.presentation.albums

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.GetAlbumsParams
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.presentation.base.DefaultPaginator
import com.babaetskv.muspert.ui.fragments.AlbumsFragmentDirections
import com.babaetskv.muspert.utils.notifier.Notifier
import io.reactivex.Single
import org.koin.core.inject

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumsView>(), DefaultPaginator.PaginatorCallback<Album> {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()
    private val catalogRepository: CatalogRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()

    private lateinit var paginator: DefaultPaginator<Album>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        paginator = DefaultPaginator(PAGE_SIZE, ::loadNext, this).apply {
            refresh()
        }
    }

    override fun onDestroy() {
        paginator.onDestroy()
        super.onDestroy()
    }

    override fun onNextLoaded(items: List<Album>) {
        viewState.showEmptyView(false)
        viewState.populateAlbums(items)
    }

    override fun onEmpty() {
        viewState.showEmptyView(true)
    }

    override fun onError(t: Throwable) {
        errorHandler.handle(t) { notifier.sendMessage(it) }
        viewState.showErrorView(true)
    }

    private fun loadNext(limit: Long, offset: Long): Single<List<Album>> {
        val params = GetAlbumsParams(
            limit = limit,
            offset = offset
        )
        return catalogRepository.getAlbums(params)
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
        val action = AlbumsFragmentDirections.actionAlbumsFragmentToPlayerFragment(PlaybackService.albumId, PlaybackService.trackId)
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
