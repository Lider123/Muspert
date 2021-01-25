package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.os.Handler
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.presentation.splash.SplashPresenter
import com.babaetskv.muspert.presentation.splash.SplashView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls

class SplashFragment : BaseFragment(), SplashView {
    @InjectPresenter
    lateinit var presenter: SplashPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_splash
    override val playbackControls: PlaybackControls?
        get() = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            presenter.onDelay()
        }, DELAY)
    }

    companion object {
        private const val DELAY = 2500L
    }
}
