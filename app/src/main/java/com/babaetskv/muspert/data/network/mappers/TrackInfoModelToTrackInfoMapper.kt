package com.babaetskv.muspert.data.network.mappers

import com.babaetskv.muspert.data.models.TrackInfo
import com.babaetskv.muspert.data.network.models.TrackInfoModel

class TrackInfoModelToTrackInfoMapper : Mapper<TrackInfoModel, TrackInfo> {

    override fun map(src: TrackInfoModel?): TrackInfo? = src?.let {
        TrackInfo(
            id = it.id,
            order = it.order
        )
    }
}
