package com.babaetskv.muspert.app.event

interface EventHub {
    fun sendEvent(event: Event, data: Any? = null)
    fun subscribe(observer: EventObserver, vararg events: Event)
    fun unsubscribe(observer: EventObserver)
}

interface EventObserver {
    fun onNextEvent(event: Event, data: Any?)
}
