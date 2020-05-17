package com.babaetskv.muspert

import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.data.repository.ProfileRepositoryImpl
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val appModule = module {
    factory { androidContext().resources }
    single {
        object : SchedulersProvider() {
            override val UI: Scheduler
                get() = AndroidSchedulers.mainThread()
            override val IO: Scheduler
                get() = Schedulers.io()
            override val COMPUTATION: Scheduler
                get() = Schedulers.computation()
        } as SchedulersProvider
    }
}

private val singletoneModule = module {
    single { User() }
}

val repositoryModule = module {
    single { ProfileRepositoryImpl(get()) as ProfileRepository }
}

val appModules = listOf(
    appModule,
    singletoneModule,
    repositoryModule
)
