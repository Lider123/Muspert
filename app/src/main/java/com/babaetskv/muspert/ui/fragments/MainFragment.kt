package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentMainBinding
import com.babaetskv.muspert.presentation.main.MainPresenter
import com.babaetskv.muspert.presentation.main.MainView
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import java.util.*

class MainFragment : PlaybackFragment(), MainView {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    private val tabFragments: MutableMap<MainTab, Fragment> = EnumMap(MainTab::class.java)
    private lateinit var binding: FragmentMainBinding

    override val layoutResId: Int
        get() = R.layout.fragment_main
    override val playbackControls: PlaybackControls
        get() = binding.viewPlaybackControls

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
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
                MainTab.CATALOG to childFragmentManager.getFragmentByTag(
                    MainTab.CATALOG,
                    CatalogFragment()
                ),
                MainTab.FEED to childFragmentManager.getFragmentByTag(
                    MainTab.FEED,
                    FeedFragment()
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
        openTab(MainTab.CATALOG.id)
    }

    private enum class MainTab(@IdRes val id: Int) {
        CATALOG(R.id.catalog),
        FEED(R.id.feed),
        PROFILE(R.id.profile)
    }
}