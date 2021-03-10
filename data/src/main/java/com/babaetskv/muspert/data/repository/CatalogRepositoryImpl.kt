package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.mappers.AlbumModelToAlbumMapper
import com.babaetskv.muspert.data.mappers.GenreModelToGenreMapper
import com.babaetskv.muspert.data.mappers.TrackInfoModelToTrackInfoMapper
import com.babaetskv.muspert.data.mappers.TrackModelToTrackMapper
import com.babaetskv.muspert.domain.model.*
import com.babaetskv.muspert.domain.repository.CatalogRepository
import io.reactivex.Single

class CatalogRepositoryImpl(
    private val authApi: AuthApi,
    private val schedulersProvider: SchedulersProvider,
    private val albumModelToAlbumMapper: AlbumModelToAlbumMapper,
    private val genreModelToGenreMapper: GenreModelToGenreMapper,
    private val trackModelToTrackMapper: TrackModelToTrackMapper,
    private val trackInfoModelToTrackInfoMapper: TrackInfoModelToTrackInfoMapper
) : CatalogRepository {

    override fun getAlbums(params: GetAlbumsParams): Single<List<Album>> =
        authApi.getAlbums(params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(albumModelToAlbumMapper::map) }

    override fun getGenres(params: GetGenresParams): Single<List<Genre>> =
        authApi.getGenres(params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(genreModelToGenreMapper::map) }

    override fun getTracks(albumId: Long): Single<List<Track>> =
        authApi.getTracks(albumId)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackModelToTrackMapper::map) }

    override fun getFavoriteTracks(params: GetFavoriteTracksParams): Single<List<Track>> =
        authApi.getFavoriteTracks(params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackModelToTrackMapper::map) }

    override fun getSearchResult(params: GetSearchResultParams): Single<List<Album>> =
        authApi.search(params.query, params.limit, params.offset)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(albumModelToAlbumMapper::map) }

    override fun getFavoriteTrackInfos(): Single<List<TrackInfo>> =
        authApi.getFavoriteTrackInfos()
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackInfoModelToTrackInfoMapper::map) }

    override fun getTrack(trackId: Long): Single<Track> =
        authApi.getTrack(trackId)
            .subscribeOn(schedulersProvider.IO)
            .map { trackModelToTrackMapper.map(it)!! }

    override fun getTrackInfos(albumId: Long): Single<List<TrackInfo>> =
        authApi.getTrackInfos(albumId)
            .subscribeOn(schedulersProvider.IO)
            .map { it.mapNotNull(trackInfoModelToTrackInfoMapper::map) }
}
