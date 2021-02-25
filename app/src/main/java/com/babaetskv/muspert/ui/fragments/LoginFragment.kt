package com.babaetskv.muspert.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.auth.AuthBuilder
import com.babaetskv.muspert.auth.PhoneAuthProvider
import com.babaetskv.muspert.databinding.FragmentLoginBinding
import com.babaetskv.muspert.presentation.login.LoginPresenter
import com.babaetskv.muspert.presentation.login.LoginView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.utils.*
import com.babaetskv.muspert.utils.notifier.Notifier
import org.koin.android.ext.android.inject

/**
 * @author Konstantin on 18.05.2020
 */
class LoginFragment : BaseFragment(), LoginView, PhoneAuthProvider.OnSendSmsListener {
    @InjectPresenter
    lateinit var presenter: LoginPresenter
    private val notifier: Notifier by inject()

    private var authBuilder: AuthBuilder? = null
    private var smsAutoFilled = false
    private lateinit var binding: FragmentLoginBinding

    override val layoutResId: Int
        get() = R.layout.fragment_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authBuilder = AuthBuilder(
            this,
            presenter::onLogin,
            presenter::onAuthCancel,
            presenter::onAuthError
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        initListeners()
        binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhone)
        setMode(LoginView.Mode.LOGIN)
        Handler().postDelayed({
            with (binding.etPhone) {
                if (text.isNullOrEmpty()) {
                    requestFocus()
                    showKeyboard()
                }
            }
        }, 500L)
    }

    override fun showAuthProgress() {
        binding.progressAuth.setVisible()
    }

    override fun hideAuthProgress() {
        binding.progressAuth.setGone()
    }

    override fun showVerificationProgress() {
        binding.progressVerification.setVisible()
    }

    override fun hideVerificationProgress() {
        binding.progressVerification.setGone()
    }

    override fun verifySms() {
        if (binding.etSmsCode.text.isEmpty()) {
            hideVerificationProgress()
            notifier.sendMessage(getString(R.string.enter_sms_code))
        } else {
            Intent().apply {
                putExtra(PhoneAuthProvider.EXTRA_SMS_CODE, binding.etSmsCode.text.toString())
            }.let {
                authBuilder?.onActivityResult(PhoneAuthProvider.REQUEST_PHONE_SIGN_IN, 0, it)
            }
        }
    }

    override fun setMode(mode: LoginView.Mode) {
        when (mode) {
            LoginView.Mode.LOGIN -> {
                binding.layoutLogin.setVisible()
                binding.layoutSms.setGone()
            }
            LoginView.Mode.SMS -> {
                binding.layoutLogin.setGone()
                binding.layoutSms.setVisible()
                Handler().postDelayed({
                    with (etSmsCode) {
                        if (text.isNullOrEmpty()) {
                            requestFocus()
                            showKeyboard()
                        }
                    }
                }, 500L)
            }
        }
    }

    override fun authPhone() {
        val phone = binding.countryCodePicker.fullNumberWithPlus
        tvCurrPhone.text = getString(R.string.current_phone_placeholder, phone)
        val params = HashMap<String, Any>()
        params[PhoneAuthProvider.PARAM_PHONE] = phone
        params[PhoneAuthProvider.PARAM_RECEIVE_SMS_CALLBACK] = this
        authBuilder?.loginPhone(params)
    }

    override fun onSmsReceived(code: String) {
        smsAutoFilled = true
        binding.etSmsCode.setText(code)
        binding.etSmsCode.setSelection(binding.etSmsCode.length())
    }

    override fun onSendSms() {
        hideAuthProgress()
        setMode(LoginView.Mode.SMS)
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            setMode(LoginView.Mode.LOGIN)
        }
        binding.loginButton.setOnClickListener {
            binding.etPhone.hideKeyboard()
            presenter.onLoginClick()
        }
        binding.etPhone.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.loginButton.performClick()
                true
            } else false
        }
        binding.etSmsCode.doOnTextChanged { text, _, _, _ ->
            if (smsAutoFilled) {
                smsAutoFilled = false
            } else if (text?.length == SMS_CODE_LENGTH) {
                binding.etSmsCode.hideKeyboard()
                presenter.onCodeEntered()
            }
        }
        binding.etPhone.doOnTextChanged { _, _, _, _ ->
            binding.etSmsCode.setText("")
        }
        binding.countryCodePicker.setPhoneNumberValidityChangeListener { isValidNumber ->
            binding.loginButton.isEnabled = isValidNumber
        }
    }

    companion object {
        private const val SMS_CODE_LENGTH = 6
    }
}
