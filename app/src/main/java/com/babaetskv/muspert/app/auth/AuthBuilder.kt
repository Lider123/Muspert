package com.babaetskv.muspert.app.auth

import android.content.Intent
import androidx.fragment.app.Fragment
import com.babaetskv.muspert.domain.model.AuthResult

class AuthBuilder(
    fragment: Fragment,
    onPhoneLogin: ((token: String) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null
) {
    private val authFabric: AuthFabric = AuthFabric()

    init {
        authFabric
            .withProvider(
                PhoneAuthProvider(fragment,
                    object : AuthProvider.Callback {

                        override fun onSuccess(authResult: AuthResult) {
                            onPhoneLogin?.invoke(authResult.token)
                        }

                        override fun onCancel() {
                            onCancel?.invoke()
                        }

                        override fun onError(message: String) {
                            onError?.invoke(message)
                        }
                    })
            )
            .init()
    }

    fun loginPhone(params: Map<String, Any>) {
        authFabric.login(PhoneAuthProvider.NAME, params)
    }

    fun destroy() {
        authFabric.destroy()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        authFabric.onActivityResult(requestCode, resultCode, data)

    fun logout() {
        authFabric.logout()
    }
}
