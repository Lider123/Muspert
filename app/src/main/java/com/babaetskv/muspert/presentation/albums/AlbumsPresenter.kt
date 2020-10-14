package com.babaetskv.muspert.presentation.albums

import com.arellomobile.mvp.InjectViewState
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.presentation.base.BasePresenter
import com.babaetskv.muspert.utils.notifier.Notifier
import com.babaetskv.muspert.viewmodel.base.RequestState
import org.koin.core.inject

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumsView>() {
    private val errorHandler: ErrorHandler by inject()
    private val notifier: Notifier by inject()

    fun onSelectAlbum(album: Album) {
        // TODO: open album fragment
        notifier.sendMessage(R.string.in_development)
    }

    fun onRequestStateChanged(state: RequestState<*>) {
        when (state) {
            is RequestState.Progress -> {
                viewState.showProgress()
                viewState.showEmptyView(false)
            }
            is RequestState.Success -> {
                viewState.hideProgress()
                viewState.showEmptyView(false)
            }
            is RequestState.Error -> {
                viewState.hideProgress()
                viewState.showErrorView(true)
                errorHandler.handle(state.error) { notifier.sendMessage(it) }
            }
            is RequestState.NoData -> {
                viewState.hideProgress()
                viewState.showEmptyView(true)
            }
        }
    }
}
