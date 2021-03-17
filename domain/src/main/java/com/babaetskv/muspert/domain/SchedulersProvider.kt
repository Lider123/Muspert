package com.babaetskv.muspert.domain

import io.reactivex.Scheduler

interface SchedulersProvider {
    val UI: Scheduler
    val IO: Scheduler
    val COMPUTATION: Scheduler
}
