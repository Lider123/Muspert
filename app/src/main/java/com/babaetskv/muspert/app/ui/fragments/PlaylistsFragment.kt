package com.babaetskv.muspert.app.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentPlaylistsBinding
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.utils.viewBinding

class PlaylistsFragment : BaseFragment() {
    private val binding: FragmentPlaylistsBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_playlists

    companion object {

        fun newInstance() = PlaylistsFragment().apply {
            arguments = bundleOf()
        }
    }
}
