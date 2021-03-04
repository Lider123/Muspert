package com.babaetskv.muspert.data.models

data class GetSearchResultParams(
    val query: String,
    val limit: Long,
    val offset: Long
)
