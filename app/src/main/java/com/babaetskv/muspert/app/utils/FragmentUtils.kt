package com.babaetskv.muspert.app.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> Fragment.argument(key: String, defaultValue: T? = null): ReadWriteProperty<Fragment, T> =
    BundleExtractorDelegate { thisRef ->
        extractFromBundle(
            bundle = thisRef.arguments,
            key = key,
            defaultValue = defaultValue
        )
    }

inline fun <reified T> extractFromBundle(
    bundle: Bundle?,
    key: String,
    defaultValue: T? = null
): T {
    val result = bundle?.get(key) ?: defaultValue
    if (result != null && result !is T) {
        throw ClassCastException("Property $key has different class type")
    }
    return result as T
}

class BundleExtractorDelegate<R, T>(private val initializer: (R) -> T) : ReadWriteProperty<R, T> {
    private var value: Any? = null

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = initializer(thisRef)
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}