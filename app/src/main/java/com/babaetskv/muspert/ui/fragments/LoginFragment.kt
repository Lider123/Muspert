package com.babaetskv.muspert.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.auth.AuthBuilder
import com.babaetskv.muspert.auth.PhoneAuthProvider
import com.babaetskv.muspert.presentation.login.LoginPresenter
import com.babaetskv.muspert.presentation.login.LoginView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.utils.*
import com.babaetskv.muspert.utils.notifier.Notifier
import kotlinx.android.synthetic.main.fragment_login.*
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
        initListeners()
        countryCodePicker.registerCarrierNumberEditText(etPhone)
        setMode(LoginView.Mode.LOGIN)
    }

    override fun onResume() {
        super.onResume()
        with (etPhone) {
            if (text.isNullOrEmpty()) {
                requestFocus()
                showKeyboard()
            }
        }
    }

    override fun showAuthProgress() {
        progressAuth.setVisible()
        loginButton.setInvisible()
    }

    override fun hideAuthProgress() {
        progressAuth.setGone()
        loginButton.setVisible()
    }

    override fun showVerificationProgress() {
        progressVerification.setVisible()
    }

    override fun hideVerificationProgress() {
        progressVerification.setGone()
    }

    override fun verifySms() {
        if (etSmsCode.text.isEmpty()) {
            hideVerificationProgress()
            notifier.sendMessage(getString(R.string.enter_sms_code))
        } else {
            Intent().apply {
                putExtra(PhoneAuthProvider.EXTRA_SMS_CODE, etSmsCode.text.toString())
            }.let {
                authBuilder?.onActivityResult(PhoneAuthProvider.REQUEST_PHONE_SIGN_IN, 0, it)
            }
        }
    }

    override fun setMode(mode: LoginView.Mode) {
        when (mode) {
            LoginView.Mode.LOGIN -> {
                layoutLogin.setVisible()
                layoutSms.setGone()
            }
            LoginView.Mode.SMS -> {
                layoutLogin.setGone()
                layoutSms.setVisible()
            }
        }
    }

    override fun authPhone() {
        val params = HashMap<String, Any>()
        params[PhoneAuthProvider.PARAM_PHONE] = countryCodePicker.fullNumberWithPlus
        params[PhoneAuthProvider.PARAM_RECEIVE_SMS_CALLBACK] = this
        authBuilder?.loginPhone(params)
    }

    override fun onSmsReceived(code: String) {
        smsAutoFilled = true
        etSmsCode.setText(code)
        etSmsCode.setSelection(etSmsCode.length())
    }

    override fun onSendSms() {
        hideAuthProgress()
        setMode(LoginView.Mode.SMS)
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            setMode(LoginView.Mode.LOGIN)
        }
        loginButton.setOnClickListener {
            etPhone.hideKeyboard()
            presenter.onLoginClick()
        }
        etPhone.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick()
                true
            } else false
        }
        etSmsCode.doOnTextChanged { text, _, _, _ ->
            if (smsAutoFilled) {
                smsAutoFilled = false
            } else if (text?.length == SMS_CODE_LENGTH) {
                etSmsCode.hideKeyboard()
                presenter.onCodeEntered()
            }
        }
        etPhone.doOnTextChanged { _, _, _, _ ->
            etSmsCode.setText("")
        }
        countryCodePicker.setPhoneNumberValidityChangeListener { isValidNumber ->
            loginButton.isEnabled = isValidNumber
        }
    }

    companion object {
        private const val SMS_CODE_LENGTH = 6
    }
}
