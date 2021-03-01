package com.babaetskv.muspert.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentPlaylistsBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

class PlaylistsFragment : BaseFragment() {
    private val binding: FragmentPlaylistsBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_playlists

    companion object {

        fun newInstance() = PlaylistsFragment().apply {
            arguments = bundleOf()
        }
    }
}
