package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.data.models.GetAlbumsParams
import com.babaetskv.muspert.data.models.GetGenresParams
import io.reactivex.Single

interface CatalogRepository {

    fun getAlbums(params: GetAlbumsParams): Single<List<Album>>

    fun getGenres(params: GetGenresParams): Single<List<Genre>>
}
