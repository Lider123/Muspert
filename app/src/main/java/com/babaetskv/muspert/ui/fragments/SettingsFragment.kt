package com.babaetskv.muspert.ui.fragments

import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls

class SettingsFragment : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_settings
    override val playbackControls: PlaybackControls?
        get() = null
}
