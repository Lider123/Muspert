package com.babaetskv.muspert.auth

import android.content.Intent
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.AuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthProvider @JvmOverloads internal constructor(
    private val fragment: Fragment,
    private val callback: AuthProvider.Callback? = null
) : AuthProvider {
    private var verificationId: String? = null
    private var smsListener: OnSendSmsListener? = null

    override val name: String
        get() = NAME

    override fun login(params: Map<String, Any>?) {
        if (params == null) return

        val phoneNumber = params[PARAM_PHONE] as String?

        if (TextUtils.isEmpty(phoneNumber)) {
            callback?.onError(fragment.getString(R.string.phone_number_is_null))
            return
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber ?: "",
            REQUEST_TIMEOUT.toLong(),
            TimeUnit.SECONDS,
            fragment.requireActivity(),
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    checkValidCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    callback?.onError(
                        when {
                            (e is FirebaseAuthInvalidCredentialsException)
                                    && (e.errorCode == ERROR_INVALID_PHONE_NUMBER) -> fragment.getString(R.string.invalid_phone_number)
                            e is FirebaseNetworkException -> fragment.getString(R.string.network_error)
                            e.message?.contains("[ 7: ]") == true -> fragment.getString(R.string.network_error)
                            else -> fragment.getString(R.string.failure_authorization)
                        }
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, forceResendingToken)
                    if (params[PARAM_RECEIVE_SMS_CALLBACK] is OnSendSmsListener) {
                        smsListener = params[PARAM_RECEIVE_SMS_CALLBACK] as OnSendSmsListener?
                        this@PhoneAuthProvider.verificationId = verificationId
                        smsListener?.onSendSms()
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != REQUEST_PHONE_SIGN_IN) return false

        val smsCode = data?.getStringExtra(EXTRA_SMS_CODE) ?: return false

        val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
        checkValidCredential(credential)
        return true
    }

    override fun logout() = FirebaseAuth.getInstance().signOut()

    private fun checkValidCredential(credential: PhoneAuthCredential) {
        credential.smsCode?.let {
            smsListener?.onSmsReceived(it)
        }

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {
                    task.result?.user
                        ?.getIdToken(true)
                        ?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful && tokenTask.result != null) {
                                callback?.onSuccess(AuthResult(tokenTask.result!!.token!!, ""))
                            } else {
                                callback?.onError(fragment.getString(R.string.failure_authorization))
                            }
                        }
                } else {
                    callback?.onError(
                        when {
                            task.exception is FirebaseNetworkException -> fragment.getString(R.string.network_error)
                            task.exception?.message?.contains("[ 7: ]") == true -> fragment.getString(R.string.network_error)
                            else -> fragment.getString(R.string.wrong_sms_code)
                        }
                    )
                }
            }
    }

    interface OnSendSmsListener {
        fun onSendSms()
        fun onSmsReceived(code: String)
    }

    companion object {
        private const val REQUEST_TIMEOUT = 120
        private const val ERROR_INVALID_PHONE_NUMBER = "ERROR_INVALID_PHONE_NUMBER"
        internal const val NAME = "PHONE_PROVIDER"
        const val PARAM_PHONE = "PHONE"
        const val PARAM_RECEIVE_SMS_CALLBACK = "RECEIVE_SMS_CALLBACK"
        const val EXTRA_SMS_CODE = "SMS_CODE"
        const val REQUEST_PHONE_SIGN_IN = 100
    }
}