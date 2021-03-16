package com.babaetskv.muspert.app.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentCacheBinding
import com.babaetskv.muspert.app.presentation.cache.CachePresenter
import com.babaetskv.muspert.app.presentation.cache.CacheView
import com.babaetskv.muspert.app.ui.base.PlaybackControls
import com.babaetskv.muspert.app.ui.base.PlaybackFragment
import com.babaetskv.muspert.app.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class CacheFragment : PlaybackFragment(), CacheView {
    private val binding: FragmentCacheBinding by viewBinding()
    private val presenter: CachePresenter by moxyPresenter {
        CachePresenter(get(), get())
    }

    override val layoutResId: Int = R.layout.fragment_cache
    override val playbackControls: PlaybackControls? = null

    companion object {

        fun newInstance() = CacheFragment().apply {
            arguments = bundleOf()
        }
    }
}
