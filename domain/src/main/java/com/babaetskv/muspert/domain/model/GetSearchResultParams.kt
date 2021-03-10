package com.babaetskv.muspert.domain.model

data class GetSearchResultParams(
    val query: String,
    val limit: Long,
    val offset: Long
)
