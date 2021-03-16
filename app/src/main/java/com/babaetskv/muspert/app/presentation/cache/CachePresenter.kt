package com.babaetskv.muspert.app.presentation.cache

import com.babaetskv.muspert.app.presentation.base.BasePresenter
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.data.ErrorHandler
import moxy.InjectViewState

@InjectViewState
class CachePresenter(
    errorHandler: ErrorHandler,
    notifier: Notifier
) : BasePresenter<CacheView>(errorHandler, notifier)
