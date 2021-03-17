package com.babaetskv.muspert.app.ui.fragments

import android.os.Bundle
import android.os.Handler
import androidx.navigation.fragment.navArgs
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentSplashBinding
import com.babaetskv.muspert.app.presentation.splash.SplashPresenter
import com.babaetskv.muspert.app.presentation.splash.SplashView
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class SplashFragment : BaseFragment(), SplashView {
    private val presenter: SplashPresenter by moxyPresenter {
        SplashPresenter(args.trackData, get(), get(), get(), get(), get(), get())
    }
    private val binding: FragmentSplashBinding by viewBinding()
    private val args: SplashFragmentArgs by navArgs()

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
