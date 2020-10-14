package com.babaetskv.muspert.viewmodel.base

sealed class RequestState<out T : Any?> {
    data class Error(val error: Throwable) : RequestState<Nothing>()
    object Success : RequestState<Nothing>()
    object Progress : RequestState<Nothing>()
    object NoData : RequestState<Nothing>()
}
