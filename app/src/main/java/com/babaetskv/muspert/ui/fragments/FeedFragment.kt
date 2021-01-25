package com.babaetskv.muspert.ui.fragments

import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls


/**
 * @author Konstantin on 13.05.2020
 */
class FeedFragment : BaseFragment() {

    override val layoutResId: Int
        get() = R.layout.fragment_feed
    override val playbackControls: PlaybackControls?
        get() = null
}
