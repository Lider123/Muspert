package com.babaetskv.muspert.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentFavoritesBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

class FavoritesFragment : BaseFragment() {
    private val binding: FragmentFavoritesBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_favorites

    companion object {

        fun newInstance() = FavoritesFragment().apply {
            arguments = bundleOf()
        }
    }
}
