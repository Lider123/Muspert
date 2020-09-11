package com.babaetskv.muspert.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun EditText.showKeyboard() {
    getSystemService(context, InputMethodManager::class.java)
        ?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    getSystemService(context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.doOnTextChanged(callback: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)) {
    addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            callback.invoke(s, start, before, count)

    })
}
