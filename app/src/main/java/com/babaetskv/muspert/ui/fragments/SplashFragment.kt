package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.os.Handler
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.presentation.splash.SplashPresenter
import com.babaetskv.muspert.presentation.splash.SplashView
import com.babaetskv.muspert.ui.base.BaseFragment

class SplashFragment : BaseFragment(), SplashView {
    @InjectPresenter
    lateinit var presenter: SplashPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            navigator.replaceWith(R.id.action_splash_to_login)
        }, DELAY)
    }

    companion object {
        private const val DELAY = 2500L
    }
}
