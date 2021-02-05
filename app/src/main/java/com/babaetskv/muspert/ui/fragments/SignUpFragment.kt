package com.babaetskv.muspert.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.presentation.signup.SignUpPresenter
import com.babaetskv.muspert.presentation.signup.SignUpView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.doOnTextChanged
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setInvisible
import com.babaetskv.muspert.utils.setVisible
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment(), SignUpView {
    @InjectPresenter
    lateinit var presenter: SignUpPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_sign_up

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun showProgress() {
        confirmProgress.setVisible()
        confirmButton.setInvisible()
    }

    override fun hideProgress() {
        confirmProgress.setGone()
        confirmButton.setVisible()
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        firstNameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.firstName = s.toString()
            confirmButton.isEnabled = presenter.dataFilled
        }
        lastNameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.lastName = s.toString()
            confirmButton.isEnabled = presenter.dataFilled
        }
        nicknameEditText.doOnTextChanged { s, _, _, _ ->
            presenter.nickname = s.toString()
            confirmButton.isEnabled = presenter.dataFilled
        }
        nicknameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onConfirm()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        confirmButton.setOnClickListener {
            presenter.onConfirm()
        }
    }
}
