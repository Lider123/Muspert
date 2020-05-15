package com.babaetskv.muspert.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.babaetskv.muspert.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    private fun setUpNavigation() {
        NavigationUI.setupWithNavController(
            bottom_nav_view,
            findNavController(R.id.navHostFragment)
        )
    }
}
