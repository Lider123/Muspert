package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCase
import io.reactivex.Single

class GetProfileUseCase(
    private val profileRepository: ProfileRepository
) : SingleUseCase<User> {

    override fun execute(): Single<User> =
        profileRepository.getProfile()
}
