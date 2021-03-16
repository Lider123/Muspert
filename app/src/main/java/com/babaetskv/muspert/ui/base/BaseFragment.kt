package com.babaetskv.muspert.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.navigation.AppNavigator
import moxy.MvpAppCompatFragment
import org.koin.android.ext.android.inject

/**
 * @author Konstantin on 13.05.2020
 */
abstract class BaseFragment : MvpAppCompatFragment() {
    protected val navigator: AppNavigator by inject()
    protected val schedulersProvider: SchedulersProvider by inject()

    private var playbackObserver: PlaybackObserver? = null
    protected open val playbackObserverInitializer: ((SchedulersProvider) -> PlaybackObserver)? = null

    @get:LayoutRes
    protected abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playbackObserver = playbackObserverInitializer?.invoke(schedulersProvider)
    }

    protected open fun onBackPressed() = requireActivity().onBackPressed()
}
