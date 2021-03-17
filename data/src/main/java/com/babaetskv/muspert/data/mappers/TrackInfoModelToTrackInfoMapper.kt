package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.TrackInfo
import com.babaetskv.muspert.data.model.TrackInfoModel

class TrackInfoModelToTrackInfoMapper : Mapper<TrackInfoModel, TrackInfo> {

    override fun map(src: TrackInfoModel?): TrackInfo? = src?.let {
        TrackInfo(
            id = it.id,
            order = it.order
        )
    }
}
