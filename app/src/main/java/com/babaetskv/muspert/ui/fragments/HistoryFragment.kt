package com.babaetskv.muspert.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentHistoryBinding
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding

class HistoryFragment : BaseFragment() {
    private val binding: FragmentHistoryBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_history

    companion object {

        fun newInstance() = HistoryFragment().apply {
            arguments = bundleOf()
        }
    }
}
