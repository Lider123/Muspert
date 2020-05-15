package com.babaetskv.muspert

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object SchedulersProvider {
    val UI: Scheduler
        get() = AndroidSchedulers.mainThread()
    val IO: Scheduler
        get() = Schedulers.io()
}