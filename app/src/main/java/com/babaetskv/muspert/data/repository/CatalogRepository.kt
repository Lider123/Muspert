package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.models.*
import io.reactivex.Single

interface CatalogRepository {

    fun getAlbums(params: GetAlbumsParams): Single<List<Album>>

    fun getGenres(params: GetGenresParams): Single<List<Genre>>

    fun getTracks(param: Long): Single<List<Track>>

    fun getFavoriteTracks(params: GetFavoriteTracksParams?): Single<List<Track>>
}
