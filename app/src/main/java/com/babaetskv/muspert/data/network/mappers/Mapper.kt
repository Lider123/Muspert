package com.babaetskv.muspert.data.network.mappers

/**
 * @author Konstantin on 26.06.2020
 */
interface Mapper<From, To> {

    fun map(src: From?): To?
}
