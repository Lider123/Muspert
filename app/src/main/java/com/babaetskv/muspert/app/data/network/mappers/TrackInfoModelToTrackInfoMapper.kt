package com.babaetskv.muspert.app.data.network.mappers

import com.babaetskv.muspert.domain.model.TrackInfo
import com.babaetskv.muspert.app.data.network.models.TrackInfoModel

class TrackInfoModelToTrackInfoMapper : Mapper<TrackInfoModel, TrackInfo> {

    override fun map(src: TrackInfoModel?): TrackInfo? = src?.let {
        TrackInfo(
            id = it.id,
            order = it.order
        )
    }
}
