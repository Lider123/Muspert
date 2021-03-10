package com.babaetskv.muspert.app.data.event

import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.app.utils.safeDispose
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class EventHubImpl(
    private val schedulersProvider: SchedulersProvider
) : EventHub {
    private val eventSubject: Subject<EventMessage> = PublishSubject.create()
    private val observers: HashMap<EventObserver, Disposable> = hashMapOf()

    override fun subscribe(observer: EventObserver, vararg events: Event) {
        val disposable = eventSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .filter { it.event in events }
            .subscribe {
                observer.onNextEvent(it.event, it.data)
            }
        observers[observer] = disposable
    }

    override fun unsubscribe(observer: EventObserver) {
        val disposable = observers[observer] ?: return

        disposable.safeDispose()
        observers.remove(observer)
    }

    override fun sendEvent(event: Event, data: Any?) {
        eventSubject.onNext(EventMessage(event, data))
    }
}
