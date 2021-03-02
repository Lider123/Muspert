package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.navArgs
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentSignUpBinding
import com.babaetskv.muspert.presentation.signup.SignUpPresenter
import com.babaetskv.muspert.presentation.signup.SignUpView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.*
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get

class SignUpFragment : BaseFragment(), SignUpView {
    private val args: SignUpFragmentArgs by navArgs()
    private val presenter: SignUpPresenter by moxyPresenter {
        SignUpPresenter(args.user, get(), get(), get(), get(), get(), get())
    }
    private val binding: FragmentSignUpBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_sign_up

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun showProgress() {
        binding.confirmProgress.setVisible()
    }

    override fun hideProgress() {
        binding.confirmProgress.setGone()
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.firstNameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.firstName = s.toString()
            binding.confirmButton.isEnabled = presenter.dataFilled
        }
        binding.lastNameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.lastName = s.toString()
            binding.confirmButton.isEnabled = presenter.dataFilled
        }
        binding.nicknameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.nickname = s.toString()
            binding.confirmButton.isEnabled = presenter.dataFilled
        }
        binding.nicknameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onConfirm()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        binding.confirmButton.setOnClickListener {
            presenter.onConfirm()
        }
    }
}
