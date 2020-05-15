package com.babaetskv.muspert

import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.data.repository.ProfileRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val appModule = module {
    factory { androidContext().resources }
}

private val singletoneModule = module {
    single { User() }
}

val repositoryModule = module {
    single { ProfileRepositoryImpl() as ProfileRepository }
}

val appModules = listOf(
    appModule,
    singletoneModule,
    repositoryModule
)
