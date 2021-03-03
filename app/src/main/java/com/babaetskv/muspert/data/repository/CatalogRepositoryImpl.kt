package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.*
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.mappers.AlbumModelToAlbumMapper
import com.babaetskv.muspert.data.network.mappers.GenreModelToGenreMapper
import com.babaetskv.muspert.data.network.mappers.TrackModelToTrackMapper
import io.reactivex.Single

class CatalogRepositoryImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider,
    private val albumModelToAlbumMapper: AlbumModelToAlbumMapper,
    private val genreModelToGenreMapper: GenreModelToGenreMapper,
    private val trackModelToTrackMapper: TrackModelToTrackMapper
) : CatalogRepository {

    override fun getAlbums(params: GetAlbumsParams): Single<List<Album>> =
        authApi.getAlbums(params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(albumModelToAlbumMapper::map) }

    override fun getGenres(params: GetGenresParams): Single<List<Genre>> =
        authApi.getGenres(params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(genreModelToGenreMapper::map) }

    override fun getTracks(param: Long): Single<List<Track>> =
        authApi.getTracks(param)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackModelToTrackMapper::map) }

    override fun getFavoriteTracks(params: GetFavoriteTracksParams?): Single<List<Track>> =
        authApi.getFavoriteTracks(params?.limit, params?.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackModelToTrackMapper::map) }
}
