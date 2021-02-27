package com.babaetskv.muspert.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.fragments.WelcomeTabFragment

class WelcomePagerAdapter(
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var currFragment: WelcomeTabFragment? = null
        private set

    override fun getCount(): Int = WelcomeTab.values().size

    override fun getItem(position: Int): Fragment = WelcomeTab.values()[position].let {
        WelcomeTabFragment.newInstance(it.layoutRes)
    }

    override fun getItemPosition(item: Any): Int {
        val fragment = item as WelcomeTabFragment
        val id = fragment.getLayoutRes()
        val tab = WelcomeTab.findByLayoutRes(id)
        val position = WelcomeTab.values().indexOf(tab)
        return if (position >= 0) position else PagerAdapter.POSITION_NONE
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, item: Any) {
        if (currFragment !== item) {
            currFragment = item as WelcomeTabFragment
        }
        super.setPrimaryItem(container, position, item)
    }

    enum class WelcomeTab(val layoutRes: Int) {
        WELCOME_1(R.layout.layout_welcome_1),
        WELCOME_2(R.layout.layout_welcome_2),
        WELCOME_3(R.layout.layout_welcome_3);

        companion object {

            fun findByLayoutRes(layoutRes: Int): WelcomeTab =
                values().find { it.layoutRes == layoutRes }!!
        }
    }
}
