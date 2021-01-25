package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.network.models.AlbumModel
import com.babaetskv.muspert.data.network.models.GenreModel
import com.babaetskv.muspert.data.network.models.TrackModel
import com.babaetskv.muspert.data.network.models.UserModel
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * @author Konstantin on 25.06.2020
 */
interface AuthApi {

    @GET("api/profile")
    fun getUser(): Single<UserModel>

    @POST("api/profile")
    fun updateUser(
        @Body profile: UserModel
    ): Completable

    @Multipart
    @POST("api/media/avatar")
    fun uploadAvatar(
        @Part image: MultipartBody.Part
    ): Completable

    @GET("api/catalog/albums")
    fun getAlbums(
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Single<List<AlbumModel>>

    @GET("api/catalog/genres")
    fun getGenres(
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Single<List<GenreModel>>

    @GET("api/catalog/tracks")
    fun getTracks(
        @Query("albumId") albumId: Long
    ): Single<List<TrackModel>>
}
