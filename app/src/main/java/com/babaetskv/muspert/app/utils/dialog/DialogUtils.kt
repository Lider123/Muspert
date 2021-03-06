package com.babaetskv.muspert.app.utils.dialog

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.babaetskv.muspert.app.R
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.showDialog(params: DialogParams) {
    val layoutId: Int = when (params) {
        is TwoChoiceDialogParams -> params.schema.layoutId
        else -> params.layoutId
    }
    val dialogView = View.inflate(this, layoutId, null)
    val dialog = BottomSheetDialog(this).apply {
        setContentView(dialogView)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    when (params) {
        is TwoChoiceDialogParams -> {
            dialogView.findViewById<TextView>(R.id.messageTextView).text = params.message
            dialogView.findViewById<Button>(R.id.leftActionButton).apply {
                text = params.leftActionText
                setOnClickListener {
                    params.onLeftActionClick.invoke(dialog)
                }
            }
            dialogView.findViewById<Button>(R.id.rightActionButton).apply {
                text = params.rightActionText
                setOnClickListener {
                    params.onRightActionClick.invoke(dialog)
                }
            }
        }
    }
    dialog.show()
}
