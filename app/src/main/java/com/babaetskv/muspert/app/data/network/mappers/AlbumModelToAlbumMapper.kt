package com.babaetskv.muspert.app.data.network.mappers

import com.babaetskv.muspert.domain.model.Album
import com.babaetskv.muspert.app.data.network.models.AlbumModel

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
