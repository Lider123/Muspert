package com.babaetskv.muspert.data.network.mappers

import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.data.network.models.AlbumModel

class AlbumModelToAlbumMapper : Mapper<AlbumModel, Album> {

    override fun map(src: AlbumModel?): Album? = src?.let {
        Album(
            id = it.id,
            title = it.title,
            cover = it.cover,
            createdAt = it.createdAt
        )
    }
}
