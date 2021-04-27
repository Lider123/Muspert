package com.babaetskv.muspert.data.repository

import com.babaetskv.muspert.data.mappers.AlbumModelToAlbumMapper
import com.babaetskv.muspert.data.mappers.GenreModelToGenreMapper
import com.babaetskv.muspert.data.mappers.TrackInfoModelToTrackInfoMapper
import com.babaetskv.muspert.data.mappers.TrackModelToTrackMapper
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.domain.SchedulersProvider
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
        listOf(
            Album(
                id = 1,
                title = "Test album",
                cover = "https://picsum.photos/200",
                artistName = "Test artist",
                createdAt = 0
            ),
            Album(
                id = 2,
                title = "Test album",
                cover = "https://picsum.photos/200",
                artistName = "Test artist",
                createdAt = 0
            ),
            Album(
                id = 3,
                title = "Test album",
                cover = "https://picsum.photos/200",
                artistName = "Test artist",
                createdAt = 0
            )
        ).let {
            Single.just(if (params.offset == 0L) it else emptyList())
        }

    override fun getFavoriteTrackInfos(): Single<List<TrackInfo>> =
        listOf(
            TrackInfo(
                id = 1,
                order = 1
            )
        ).let {
            Single.just(it)
        }

    override fun getFavoriteTracks(params: GetFavoriteTracksParams): Single<List<Track>> =
        listOf(
            Track(
                id = 1,
                title = "Test track",
                link = "http://k007.kiwi6.com/hotlink/lvezo2hdo1/The_Be_r_de_Peanut.mp3",
                albumId = 1,
                position = 1,
                cover = "https://picsum.photos/200",
                albumTitle = "Test album",
                artistName = "Test artist",
                isFavorite = true
            )
        ).let {
            Single.just(if (params.offset == 0L) it else emptyList())
        }

    override fun getGenres(params: GetGenresParams): Single<List<Genre>> =
        listOf(
            Genre(
                id = 1,
                title = "Test genre",
                image = "https://picsum.photos/200"
            )
        ).let {
            Single.just(if (params.offset == 0L) it else emptyList())
        }

    override fun getSearchResult(params: GetSearchResultParams): Single<List<Album>> =
        getAlbums(GetAlbumsParams(params.limit, params.offset))
            .map {  albums ->
                albums.filter { it.title.contains(params.query, ignoreCase = true) }
            }

    override fun getTrack(trackId: Long): Single<Track> =
        Track(
            id = 1,
            title = "Test track",
            link = "http://k007.kiwi6.com/hotlink/lvezo2hdo1/The_Be_r_de_Peanut.mp3",
            albumId = 1,
            position = 1,
            cover = "https://picsum.photos/200",
            albumTitle = "Test album",
            artistName = "Test artist",
            isFavorite = false
        ).let {
            Single.just(it)
        }

    override fun getTrackInfos(albumId: Long): Single<List<TrackInfo>> =
        listOf(
            TrackInfo(
                id = 1,
                order = 1
            ),
            TrackInfo(
                id = 2,
                order = 1
            ),
            TrackInfo(
                id = 3,
                order = 1
            )
        ).let {
            Single.just(it)
        }

    override fun getTracks(albumId: Long): Single<List<Track>> =
        listOf(
            Track(
                id = 1,
                title = "Test track",
                link = "http://k007.kiwi6.com/hotlink/lvezo2hdo1/The_Be_r_de_Peanut.mp3",
                albumId = 1,
                position = 1,
                cover = "https://picsum.photos/200",
                albumTitle = "Test album",
                artistName = "Test artist",
                isFavorite = false
            ),
            Track(
                id = 2,
                title = "Test track",
                link = "http://k007.kiwi6.com/hotlink/lvezo2hdo1/The_Be_r_de_Peanut.mp3",
                albumId = 1,
                position = 1,
                cover = "https://picsum.photos/200",
                albumTitle = "Test album",
                artistName = "Test artist",
                isFavorite = false
            ),
            Track(
                id = 3,
                title = "Test track",
                link = "http://k007.kiwi6.com/hotlink/lvezo2hdo1/The_Be_r_de_Peanut.mp3",
                albumId = 1,
                position = 1,
                cover = "https://picsum.photos/200",
                albumTitle = "Test album",
                artistName = "Test artist",
                isFavorite = false
            )
        ).let {
            Single.just(it)
        }
}
