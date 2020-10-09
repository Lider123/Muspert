package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    override val layoutResId: Int
        get() = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
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
}