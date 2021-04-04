package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.data.network.model.TrackModel

class TrackModelToTrackMapper : Mapper<TrackModel, Track> {

    override fun map(src: TrackModel?): Track? = src?.let {
        Track(
            id = it.id,
            title = it.title,
            link = it.link,
            albumId = it.albumId,
            cover = it.cover,
            albumTitle = it.albumTitle,
            artistName = it.artistName,
            position = it.position,
            isFavorite = it.isFavorite
        )
    }
}
