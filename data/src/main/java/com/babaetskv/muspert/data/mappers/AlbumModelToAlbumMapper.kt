package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.data.network.model.AlbumModel

class AlbumModelToAlbumMapper : Mapper<AlbumModel, Album> {

    override fun map(src: AlbumModel?): Album? = src?.let {
        Album(
            id = it.id,
            title = it.title,
            cover = it.cover,
            artistName = it.artistName,
            createdAt = it.createdAt
        )
    }
}
