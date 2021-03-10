package com.babaetskv.muspert.app.data.network.mappers

import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.app.data.network.models.GenreModel

class GenreModelToGenreMapper : Mapper<GenreModel, Genre> {

    override fun map(src: GenreModel?): Genre? = src?.let {
        Genre(
            id = it.id,
            title = it.title,
            image = it.image
        )
    }
}
