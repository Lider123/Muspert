package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import com.babaetskv.muspert.R
import com.babaetskv.muspert.adapter.LibraryPagerAdapter
import com.babaetskv.muspert.databinding.FragmentLibraryBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

class LibraryFragment : BaseFragment() {
    private val binding: FragmentLibraryBinding by viewBinding()
    private lateinit var adapter: LibraryPagerAdapter

    override val layoutResId: Int
        get() = R.layout.fragment_library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewpager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewpager)
    }

    private fun initAdapter() {
        adapter = LibraryPagerAdapter(resources, childFragmentManager)
    }
}