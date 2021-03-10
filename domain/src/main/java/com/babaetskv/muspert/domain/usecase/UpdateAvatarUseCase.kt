package com.babaetskv.muspert.domain.usecase

import android.net.Uri
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.domain.usecase.base.CompletableUseCaseWithParams
import io.reactivex.Completable

class UpdateAvatarUseCase(
    private val profileRepository: ProfileRepository
) : CompletableUseCaseWithParams<Uri> {

    override fun execute(params: Uri): Completable =
        profileRepository.updateAvatar(params)
}
