package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.model.*
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

    @POST("api/favorites")
    fun addToFavorites(
        @Body request: AddToFavoritesRequest
    ): Completable

    @DELETE("api/favorites/{trackId}")
    fun removeFromFavorites(
        @Path("trackId") trackId: Long
    ): Completable

    @GET("api/catalog/favorites")
    fun getFavoriteTracks(
        @Query("limit") limit: Long?,
        @Query("offset") offset: Long?
    ): Single<List<TrackModel>>

    @GET("api/search")
    fun search(
        @Query("query") query: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): Single<List<AlbumModel>>

    @GET("api/catalog/favorites/info")
    fun getFavoriteTrackInfos(): Single<List<TrackInfoModel>>

    @GET("api/catalog/tracks/{trackId}")
    fun getTrack(
        @Path("trackId") trackId: Long
    ): Single<TrackModel>

    @GET("api/catalog/tracks/info")
    fun getTrackInfos(
        @Query("albumId") albumId: Long
    ): Single<List<TrackInfoModel>>
}
