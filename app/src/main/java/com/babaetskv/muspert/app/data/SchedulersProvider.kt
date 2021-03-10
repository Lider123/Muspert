package com.babaetskv.muspert.app.data

import io.reactivex.Scheduler

abstract class SchedulersProvider {
    abstract val UI: Scheduler
    abstract val IO: Scheduler
    abstract val COMPUTATION: Scheduler
}
