package com.babaetskv.muspert.app.event

enum class Event {
    FAVORITES_UPDATE
}

data class EventMessage(
    val event: Event,
    val data: Any?
)
