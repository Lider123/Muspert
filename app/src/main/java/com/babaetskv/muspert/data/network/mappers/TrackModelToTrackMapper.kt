package com.babaetskv.muspert.data.network.mappers

import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.network.models.TrackModel

class TrackModelToTrackMapper : Mapper<TrackModel, Track> {

    override fun map(src: TrackModel?): Track? = src?.let {
        Track(
            id = it.id,
            title = it.title,
            link = it.link,
            albumId = it.albumId
        )
    }
}