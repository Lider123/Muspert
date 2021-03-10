package com.babaetskv.muspert.app.viewmodel.albums

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.GetAlbumsParams
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.app.viewmodel.base.BaseDataSource
import com.babaetskv.muspert.app.viewmodel.base.RequestState
import io.reactivex.disposables.CompositeDisposable

@Deprecated("Use pagination instead")
class AlbumsDataSource(
    private val compositeDisposable: CompositeDisposable,
    private val catalogRepository: CatalogRepository,
    private val schedulersProvider: SchedulersProvider
) : BaseDataSource<Album>() {

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Album>
    ) {
        GetAlbumsParams(
            limit = params.requestedLoadSize.toLong(),
            offset = 0
        ).let {
            catalogRepository.getAlbums(it)
                .observeOn(schedulersProvider.UI)
                .doOnSubscribe {
                    updateState(RequestState.Progress)
                }
                .subscribe({ albums ->
                    if (albums.isEmpty()) {
                        updateState(RequestState.NoData)
                    } else {
                        updateState(RequestState.Success)
                        callback.onResult(albums, null, 2L)
                    }
                }, ::onError)
        }.let {
            compositeDisposable.add(it)
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Album>) {
        val key = params.key
        val pageSize = params.requestedLoadSize
        GetAlbumsParams(
            limit = pageSize.toLong(),
            offset = pageSize * (key - 1)
        ).let {
            catalogRepository.getAlbums(it)
                .observeOn(schedulersProvider.UI)
                .doOnSubscribe {
                    updateState(RequestState.Progress)
                }
                .subscribe({ albums ->
                    updateState(RequestState.Success)
                    callback.onResult(albums, key + 1)
                }, ::onError)
        }.let {
            compositeDisposable.add(it)
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Album>) = Unit

    private fun onError(t: Throwable) {
        updateState(RequestState.Error(t))
    }

    class Factory(
        private val compositeDisposable: CompositeDisposable,
        private val catalogRepository: CatalogRepository,
        private val schedulersProvider: SchedulersProvider
    ) : DataSource.Factory<Long, Album>() {
        val albumsDataSourceLiveData = MutableLiveData<AlbumsDataSource>()

        override fun create(): DataSource<Long, Album> = AlbumsDataSource(compositeDisposable, catalogRepository, schedulersProvider).also {
            albumsDataSourceLiveData.postValue(it)
        }
    }
}