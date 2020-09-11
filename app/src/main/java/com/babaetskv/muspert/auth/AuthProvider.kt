package com.babaetskv.muspert.auth

import android.content.Intent
import com.babaetskv.muspert.data.models.AuthResult

interface AuthProvider {
    val name: String

    fun init() = Unit

    fun login(params: Map<String, Any>?)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean

    fun logout()

    fun destroy() {}

    interface Callback {

        fun onSuccess(authResult: AuthResult) = Unit

        fun onCancel() = Unit

        fun onError(message: String) = Unit
    }
}