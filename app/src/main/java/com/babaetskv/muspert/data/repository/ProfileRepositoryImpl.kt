package com.babaetskv.muspert.data.repository

import android.net.Uri
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.mappers.UserModelToUserMapper
import com.babaetskv.muspert.data.network.mappers.UserToUserModelMapper
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI

class ProfileRepositoryImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider,
    private val userModelToUserMapper: UserModelToUserMapper,
    private val userToUserModelMapper: UserToUserModelMapper
) : ProfileRepository {

    override fun getProfile(): Single<User> =
        authApi.getUser()
            .subscribeOn(schedulersProvider.IO)
            .map(userModelToUserMapper::map)

    override fun updateProfile(profile: User): Completable =
        userToUserModelMapper.map(profile)!!.let {
            authApi.updateUser(it)
        }
            .subscribeOn(schedulersProvider.IO)

    override fun updateAvatar(imageUri: Uri): Completable =
        imageUri.toString().let {
            if (it.isNotEmpty() && it.startsWith("file://")) {
                authApi.uploadAvatar(getImagePart(it))
                    .subscribeOn(schedulersProvider.IO)
            } else Completable.complete()
        }

    private fun getImagePart(path: String): MultipartBody.Part {
        val file = File(URI.create(path))
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }
}
