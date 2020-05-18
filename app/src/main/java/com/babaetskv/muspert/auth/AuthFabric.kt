package com.babaetskv.muspert.auth

import android.content.Intent

class AuthFabric {
    private val providers: MutableMap<String, AuthProvider>

    init {
        providers = HashMap()
    }

    operator fun get(key: String): AuthProvider? {
        return if (providers.containsKey(key)) providers[key] else null
    }

    fun init() {
        for (provider in providers) provider.value.init()
    }

    fun withProvider(provider: AuthProvider?): AuthFabric {
        provider?.let {
            providers[provider.name] = provider
        }
        return this
    }

    fun login(key: String, params: Map<String, Any>?) {
        if (!providers.containsKey(key)) return

        providers[key]?.login(params)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        for (provider in providers) {
            if (provider.value.onActivityResult(requestCode, resultCode, data)) return true
        }
        return false
    }

    fun logout(key: String) {
        providers[key]?.logout()
    }

    fun logout() {
        for (provider in providers) provider.value.logout()
    }

    fun destroy() {
        for (provider in providers) provider.value.destroy()
    }
}
