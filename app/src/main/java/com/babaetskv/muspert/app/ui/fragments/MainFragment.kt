package com.babaetskv.muspert.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentMainBinding
import com.babaetskv.muspert.app.presentation.main.MainPresenter
import com.babaetskv.muspert.app.presentation.main.MainView
import com.babaetskv.muspert.app.ui.base.PlaybackControls
import com.babaetskv.muspert.app.ui.base.PlaybackFragment
import com.babaetskv.muspert.app.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import java.util.*

class MainFragment : PlaybackFragment(), MainView {
    private val presenter: MainPresenter by moxyPresenter {
        MainPresenter(get(), get(), get())
    }
    private val tabFragments: MutableMap<MainTab, Fragment> = EnumMap(MainTab::class.java)
    private val binding: FragmentMainBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_main
    override val playbackControls: PlaybackControls
        get() = binding.viewPlaybackControls

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initTabs()
        initBottomNavigation()
    }

    override fun openTab(id: Int) {
        childFragmentManager.commitNow {
            for (tabFragment in tabFragments) {
                if (tabFragment.key.id == id) attach(tabFragment.value) else detach(tabFragment.value)
            }
        }
    }

    private fun initListeners() {
        binding.viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
    }

    private fun initTabs() {
        tabFragments.clear()
        tabFragments.putAll(
            mapOf(
                MainTab.HOME to childFragmentManager.getFragmentByTag(
                    MainTab.HOME,
                    HomeFragment()
                ),
                MainTab.SEARCH to childFragmentManager.getFragmentByTag(
                    MainTab.SEARCH,
                    SearchFragment()
                ),
                MainTab.LIBRARY to childFragmentManager.getFragmentByTag(
                    MainTab.LIBRARY,
                    LibraryFragment()
                ),
                MainTab.PROFILE to childFragmentManager.getFragmentByTag(
                    MainTab.PROFILE,
                    ProfileFragment()
                )
            )
        )
    }

    private fun FragmentManager.getFragmentByTag(tag: MainTab, defaultValue: Fragment): Fragment =
        findFragmentByTag(tag.name) ?: defaultValue.also {
            commitNow {
                add(R.id.container, it, tag.name)
                detach(it)
            }
        }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            presenter.onBottomNavigate(it)
            true
        }
        openTab(MainTab.HOME.id)
    }

    private enum class MainTab(@IdRes val id: Int) {
        HOME(R.id.home),
        SEARCH(R.id.search),
        LIBRARY(R.id.library),
        PROFILE(R.id.profile)
    }
}