package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.os.Handler
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.BaseFragment

class SplashFragment : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            NavOptions.Builder()
                .setPopUpTo(R.id.splash, true)
                .build()
                .let {
                    findNavController().navigate(R.id.action_splash_to_main, null, it)
                }
        }, DELAY)
    }

    companion object {
        private const val DELAY = 2500L
    }
}
