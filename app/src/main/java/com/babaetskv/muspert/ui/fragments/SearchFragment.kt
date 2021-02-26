package com.babaetskv.muspert.ui.fragments

import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentSearchBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

/**
 * @author Konstantin on 13.05.2020
 */
class SearchFragment : BaseFragment() {
    private val binding: FragmentSearchBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_search
}
