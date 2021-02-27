package com.babaetskv.muspert.ui.fragments

import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.argument

class WelcomeTabFragment : BaseFragment() {

    override val layoutResId: Int by argument(ARG_LAYOUT_RES)

    override fun onBackPressed() = Unit

    fun getLayoutRes(): Int = layoutResId

    companion object {
        private const val ARG_LAYOUT_RES = "ARG_LAYOUT_RES"

        fun newInstance(@LayoutRes layoutRes: Int) = WelcomeTabFragment().apply {
            arguments = bundleOf(
                ARG_LAYOUT_RES to layoutRes
            )
        }
    }
}
