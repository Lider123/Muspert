package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.models.*
import io.reactivex.Single

interface CatalogRepository {

    fun getAlbums(params: GetAlbumsParams): Single<List<Album>>

    fun getGenres(params: GetGenresParams): Single<List<Genre>>

    fun getTracks(albumId: Long): Single<List<Track>>

    fun getFavoriteTracks(params: GetFavoriteTracksParams): Single<List<Track>>

    fun getSearchResult(params: GetSearchResultParams): Single<List<Album>>

    fun getTrack(trackId: Long): Single<Track>

    fun getTrackInfos(albumId: Long): Single<List<TrackInfo>>

    fun getFavoriteTrackInfos(): Single<List<TrackInfo>>
}
