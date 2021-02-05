package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.presentation.main.MainPresenter
import com.babaetskv.muspert.presentation.main.MainView
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : PlaybackFragment(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_main
    override val playbackControls: PlaybackControls
        get() = viewPlaybackControls

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initBottomNavigation()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment = when (item.itemId) {
            R.id.catalog -> CatalogFragment()
            R.id.feed -> FeedFragment()
            R.id.profile -> ProfileFragment()
            else -> Fragment()
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.container, selectedFragment)
            .commit()
        return true
    }

    private fun initListeners() {
        viewPlaybackControls.setOnClickListener {
            presenter.onPlaybackControlsClick()
        }
    }

    private fun initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.catalog
    }
}