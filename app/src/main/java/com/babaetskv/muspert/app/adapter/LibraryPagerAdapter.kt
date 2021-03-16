package com.babaetskv.muspert.app.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.babaetskv.muspert.app.ui.fragments.FavoritesFragment
import com.babaetskv.muspert.app.ui.fragments.HistoryFragment
import com.babaetskv.muspert.app.ui.fragments.PlaylistsFragment
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.ui.fragments.CacheFragment

class LibraryPagerAdapter(
    private val resources: Resources,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = LibraryTab.values().size

    override fun getItem(position: Int): Fragment =
        LibraryTab.values()[position].fragmentFactory.invoke()

    override fun getPageTitle(position: Int): CharSequence =
        LibraryTab.values()[position].titleFactory.invoke(resources)

    enum class LibraryTab(
        val titleFactory: (resources: Resources) -> String,
        val fragmentFactory: () -> Fragment
    ) {
        PLAYLISTS(
            titleFactory = { it.getString(R.string.playlists) },
            fragmentFactory = { PlaylistsFragment.newInstance() }
        ),
        FAVORITES(
            titleFactory = { it.getString(R.string.favorites) },
            fragmentFactory = { FavoritesFragment.newInstance() }
        ),
        CACHE(
            titleFactory = { it.getString(R.string.cache) },
            fragmentFactory = { CacheFragment.newInstance() }
        ),
        HISTORY(
            titleFactory = { it.getString(R.string.history) },
            fragmentFactory = { HistoryFragment.newInstance() }
        )
    }
}
