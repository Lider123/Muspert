package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {
    private lateinit var bottomNavController: NavController

    override val layoutResId: Int
        get() = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        val fragmentContainer = view!!.findViewById<View>(R.id.bottomNavHostFragment)
        bottomNavController = Navigation.findNavController(fragmentContainer!!)
        NavigationUI.setupWithNavController(bottomNavigationView, bottomNavController)
    }
}