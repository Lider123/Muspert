package com.babaetskv.muspert.app.viewmodel.albums

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.app.viewmodel.base.BaseViewModel
import com.babaetskv.muspert.app.viewmodel.base.RequestState

@Deprecated("Use presenter instead")
class AlbumsViewModel(
    catalogRepository: CatalogRepository,
    schedulersProvider: SchedulersProvider
) : BaseViewModel() {
    private val albumsDataSourceFactory =
        AlbumsDataSource.Factory(compositeDisposable, catalogRepository, schedulersProvider)

    var albumsLiveData: LiveData<PagedList<Album>>

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .setPrefetchDistance(3)
            .build()
        albumsLiveData = LivePagedListBuilder(albumsDataSourceFactory, config).build()
    }

    private fun setState(state: RequestState<*>) {
        albumsDataSourceFactory.albumsDataSourceLiveData.value?.updateState(state)
    }

    fun getState(): LiveData<RequestState<Any?>> =
        Transformations.switchMap<AlbumsDataSource, RequestState<*>>(
            albumsDataSourceFactory.albumsDataSourceLiveData,
            AlbumsDataSource::state
        )

    fun updateAlbums() = albumsDataSourceFactory.albumsDataSourceLiveData.value?.invalidate()

    companion object {
        private const val PAGE_SIZE = 10
    }
}
