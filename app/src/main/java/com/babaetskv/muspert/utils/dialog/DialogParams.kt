package com.babaetskv.muspert.utils.dialog

import android.app.Dialog
import com.babaetskv.muspert.R

abstract class DialogParams {
    abstract val layoutId: Int
}

data class TwoChoiceDialogParams(
    val message: String,
    val leftActionText: String,
    val rightActionText: String,
    val onLeftActionClick: (dialog: Dialog) -> Unit,
    val onRightActionClick: (dialog: Dialog) -> Unit
) : DialogParams() {
    override val layoutId: Int
        get() = R.layout.dialog_two_choice
}
