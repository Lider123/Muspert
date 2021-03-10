package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.domain.usecase.base.CompletableUseCaseWithParams
import io.reactivex.Completable

class UpdateProfileUseCase(
    private val profileRepository: ProfileRepository
) : CompletableUseCaseWithParams<User> {

    override fun execute(params: User): Completable =
        profileRepository.updateProfile(params)
}
