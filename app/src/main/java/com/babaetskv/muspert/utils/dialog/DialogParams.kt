package com.babaetskv.muspert.utils.dialog

import android.app.Dialog
import androidx.annotation.LayoutRes
import com.babaetskv.muspert.R

abstract class DialogParams {
    abstract val layoutId: Int
}

data class TwoChoiceDialogParams(
    val schema: Schema,
    val message: String,
    val leftActionText: String,
    val rightActionText: String,
    val onLeftActionClick: (dialog: Dialog) -> Unit,
    val onRightActionClick: (dialog: Dialog) -> Unit
) : DialogParams() {
    override val layoutId: Int
        get() = R.layout.dialog_two_choice_both_accent

    enum class Schema(@LayoutRes val layoutId: Int) {
        ACCENT_LEFT(R.layout.dialog_two_choice_left_accent),
        ACCENT_RIGHT(R.layout.dialog_two_choice_right_accent),
        ACCENT_BOTH(R.layout.dialog_two_choice_both_accent)
    }
}
