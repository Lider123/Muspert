package com.babaetskv.muspert.app.ui.fragments

import android.os.Bundle
import android.view.View
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.adapter.WelcomePagerAdapter
import com.babaetskv.muspert.app.databinding.FragmentWelcomeBinding
import com.babaetskv.muspert.app.presentation.welcome.WelcomePresenter
import com.babaetskv.muspert.app.presentation.welcome.WelcomeView
import com.babaetskv.muspert.app.ui.base.BaseFragment
import com.babaetskv.muspert.app.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class WelcomeFragment : BaseFragment(), WelcomeView {
    private val presenter: WelcomePresenter by moxyPresenter {
        WelcomePresenter(get(), get(), get(), get())
    }
    private lateinit var adapter: WelcomePagerAdapter
    private val binding: FragmentWelcomeBinding by viewBinding()

    override val layoutResId: Int = R.layout.fragment_welcome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initListeners()
    }

    override fun showNextPage() {
        binding.viewpager.currentItem++
    }

    private fun initViewPager() {
        binding.viewpager.apply {
            adapter = this@WelcomeFragment.adapter
            currentItem = 0
            binding.dotsIndicator.setViewPager(this)
        }
    }

    private fun initListeners() {
        binding.btnSkip.setOnClickListener {
            presenter.finishWelcome()
        }
        binding.btnNext.setOnClickListener {
            val itemsCount = adapter.count
            if (binding.viewpager.currentItem < itemsCount - 1) {
                presenter.onNext()
            } else {
                presenter.finishWelcome()
            }
        }
    }

    private fun initAdapter() {
        adapter = WelcomePagerAdapter(childFragmentManager)
    }
}
