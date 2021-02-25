package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.FragmentSignUpBinding
import com.babaetskv.muspert.presentation.signup.SignUpPresenter
import com.babaetskv.muspert.presentation.signup.SignUpView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.doOnTextChanged
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setInvisible
import com.babaetskv.muspert.utils.setVisible

class SignUpFragment : BaseFragment(), SignUpView {
    @InjectPresenter
    lateinit var presenter: SignUpPresenter

    private val args: SignUpFragmentArgs by navArgs()
    private lateinit var binding: FragmentSignUpBinding

    override val layoutResId: Int
        get() = R.layout.fragment_sign_up

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)
        initListeners()
    }

    override fun showProgress() {
        binding.confirmProgress.setVisible()
        binding.confirmButton.setInvisible()
    }

    override fun hideProgress() {
        binding.confirmProgress.setGone()
        binding.confirmButton.setVisible()
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

    @ProvidePresenter
    fun providePresenter() = SignUpPresenter(args.user)
}
