package com.babaetskv.muspert.app.presentation.search

import com.babaetskv.muspert.app.ErrorHandler
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.domain.model.GetSearchResultParams
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.presentation.base.DefaultPaginator
import com.babaetskv.muspert.app.ui.fragments.MainFragmentDirections
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.GetSearchResultUseCase
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class SearchPresenter(
    private val getSearchResult: GetSearchResultUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val navigator: AppNavigator,
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<SearchView>(errorHandler, notifier), DefaultPaginator.PaginatorCallback<Album> {
    private var query = ""
    private lateinit var paginator: DefaultPaginator<Album>
    private val queryChangingSubject: Subject<String> = BehaviorSubject.createDefault("")

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        queryChangingSubject
            .subscribeOn(schedulersProvider.IO)
            .debounce(1L, TimeUnit.SECONDS)
            .observeOn(schedulersProvider.UI)
            .subscribe {
                query = it
                refreshSearchResults()
            }
            .unsubscribeOnDestroy()
        paginator = DefaultPaginator(PAGE_SIZE, ::loadNext, this)
        refreshSearchResults()
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
        val params = GetSearchResultParams(
            query = query,
            limit = limit,
            offset = offset
        )
        return getSearchResult.execute(params)
            .observeOn(schedulersProvider.UI)
            .doOnSubscribe {
                viewState.showProgress()
            }
            .doAfterTerminate {
                viewState.hideProgress()
            }
    }

    fun onSelectAlbum(album: Album) {
        val action = MainFragmentDirections.actionMainFragmentToTracksFragment(album)
        navigator.forward(action)
    }

    fun refreshSearchResults() {
        if (query.isEmpty()) {
            viewState.showPresearchView(true)
        } else {
            viewState.showPresearchView(false)
            paginator.refresh()
        }
    }

    fun loadNextPage() {
        paginator.loadNextPage()
    }

    fun onClear() {
        query = ""
        viewState.populateQuery(query)
    }

    fun onSearchQueryChanged(query: String) {
        queryChangingSubject.onNext(query)
    }

    companion object {
        private const val PAGE_SIZE = 6
    }
}
