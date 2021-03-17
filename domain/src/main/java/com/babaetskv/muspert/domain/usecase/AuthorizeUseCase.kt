package com.babaetskv.muspert.domain.usecase

import com.babaetskv.muspert.domain.gateway.AuthGateway
import com.babaetskv.muspert.domain.model.User
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.domain.usecase.base.SingleUseCaseWithParams
import io.reactivex.Single

class AuthorizeUseCase(
    private val authGateway: AuthGateway,
    private val profileRepository: ProfileRepository
): SingleUseCaseWithParams<String, User> {

    override fun execute(params: String): Single<User> =
        authGateway.authorize(params)
            .andThen(profileRepository.getProfile())
}