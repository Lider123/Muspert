package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.os.Handler
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentSplashBinding
import com.babaetskv.muspert.presentation.splash.SplashPresenter
import com.babaetskv.muspert.presentation.splash.SplashView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class SplashFragment : BaseFragment(), SplashView {
    private val presenter: SplashPresenter by moxyPresenter {
        SplashPresenter(get(), get(), get(), get(), get(), get())
    }
    private val binding: FragmentSplashBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_splash

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
