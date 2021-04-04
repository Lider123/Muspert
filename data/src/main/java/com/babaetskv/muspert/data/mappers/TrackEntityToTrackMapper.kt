package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.data.db.entity.TrackEntity
import com.babaetskv.muspert.domain.model.Track

class TrackEntityToTrackMapper : Mapper<TrackEntity, Track> {

    override fun map(src: TrackEntity?): Track? = src?.let {
        Track(
            id = it.id,
            title = it.title,
            link = it.path,
            albumId = it.albumId,
            position = it.createdAt,
            cover = it.coverPath,
            albumTitle = it.albumTitle,
            artistName = it.artistName,
            isFavorite = false
        )
    }
}
