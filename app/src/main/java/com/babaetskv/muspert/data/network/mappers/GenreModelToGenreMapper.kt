package com.babaetskv.muspert.data.network.mappers

import com.babaetskv.muspert.data.models.Genre
import com.babaetskv.muspert.data.network.models.GenreModel

class GenreModelToGenreMapper : Mapper<GenreModel, Genre> {

    override fun map(src: GenreModel?): Genre? = src?.let {
        Genre(
            id = it.id,
            title = it.title,
            image = it.image
        )
    }
}
