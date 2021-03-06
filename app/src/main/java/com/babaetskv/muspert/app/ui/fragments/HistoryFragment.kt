package com.babaetskv.muspert.app.ui.fragments

import androidx.core.os.bundleOf
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentHistoryBinding
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.utils.viewBinding

class HistoryFragment : BaseFragment() {
    private val binding: FragmentHistoryBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_history

    companion object {

        fun newInstance() = HistoryFragment().apply {
            arguments = bundleOf()
        }
    }
}
