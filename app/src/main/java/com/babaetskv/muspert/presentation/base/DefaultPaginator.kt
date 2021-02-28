package com.babaetskv.muspert.presentation.base

import com.babaetskv.muspert.utils.safeDispose
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class DefaultPaginator<T>(
    private val pageSize: Int,
    private val nextPageLoader: (Long, Long) -> Single<List<T>>,
    private val callback: PaginatorCallback<T>
) {
    private var currPage = 0
    private var lastPageLoaded = false
    private var currData = mutableListOf<T>()
    private val disposable = CompositeDisposable()

    private fun onNextLoaded(items: List<T>) {
        currPage++
        currData.addAll(items)
        if (items.isEmpty()) {
            if (currPage <= 1) callback.onEmptyData()
            lastPageLoaded = true
        } else {
            callback.onNextLoaded(currData)
        }
    }

    private fun onError(t: Throwable) {
        callback.onPagingError(t)
    }

    fun onDestroy() {
        disposable.safeDispose()
    }

    fun loadNextPage() {
        if (lastPageLoaded) return

        nextPageLoader.invoke(pageSize.toLong(), pageSize.toLong() * currPage)
            .subscribe(::onNextLoaded, ::onError)
            .let {
                disposable.add(it)
            }
    }

    fun refresh() {
        currPage = 0
        lastPageLoaded = false
        currData.clear()
        loadNextPage()
    }

    interface PaginatorCallback<T> {
        fun onNextLoaded(items: List<T>)
        fun onEmptyData()
        fun onPagingError(t: Throwable)
    }
}
