package com.babaetskv.muspert.data.mappers

import com.babaetskv.muspert.domain.model.Genre
import com.babaetskv.muspert.data.model.GenreModel

class GenreModelToGenreMapper : Mapper<GenreModel, Genre> {

    override fun map(src: GenreModel?): Genre? = src?.let {
        Genre(
            id = it.id,
            title = it.title,
            image = it.image
        )
    }
}
