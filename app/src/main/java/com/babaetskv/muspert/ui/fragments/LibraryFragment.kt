package com.babaetskv.muspert.ui.fragments

import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentLibraryBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

class LibraryFragment : BaseFragment() {
    private val binding: FragmentLibraryBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_library
}