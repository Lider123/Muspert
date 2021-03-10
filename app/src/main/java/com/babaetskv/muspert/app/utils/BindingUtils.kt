package com.babaetskv.muspert.app.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding() =
    ActivityBindingDelegate(this, T::class.java)

inline fun <reified T : ViewBinding> ViewGroup.viewBinding() =
    ViewGroupBindingDelegate(this, T::class.java)

inline fun <reified T : ViewBinding> Fragment.viewBinding() =
    FragmentBindingDelegate(this, T::class.java)

class FragmentBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val binder: Class<T>
) : ReadOnlyProperty<Fragment, T>, LifecycleObserver {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (binding == null) {
            thisRef.lifecycle.addObserver(this)
            binding = binder.getMethod("bind", View::class.java)!!.invoke(null, fragment.view) as T
        }
        return binding!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        fragment.lifecycle.removeObserver(this)
        binding = null
    }
}

class ViewGroupBindingDelegate<T : ViewBinding>(
    private val viewGroup: ViewGroup,
    private val binder: Class<T>
) : ReadOnlyProperty<ViewGroup, T> {
    private var binding: T? = null

    override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T {
        if (binding == null) {
            binding = binder.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )!!.invoke(null, LayoutInflater.from(viewGroup.context), viewGroup, true) as T
        }
        return binding!!
    }
}

class ActivityBindingDelegate<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val binder: Class<T>
) : ReadOnlyProperty<AppCompatActivity, T>, LifecycleObserver {
    private var binding: T? = null

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        if (binding == null) {
            activity.lifecycle.addObserver(this)
            binding = binder.getMethod("inflate", LayoutInflater::class.java)!!.invoke(null, thisRef.layoutInflater) as T
        }
        return binding!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        activity.lifecycle.removeObserver(this)
        binding = null
    }
}
