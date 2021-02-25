package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentFeedBinding
import com.babaetskv.muspert.ui.base.BaseFragment

/**
 * @author Konstantin on 13.05.2020
 */
class FeedFragment : BaseFragment() {
    private lateinit var binding: FragmentFeedBinding

    override val layoutResId: Int
        get() = R.layout.fragment_feed

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)
    }
}
