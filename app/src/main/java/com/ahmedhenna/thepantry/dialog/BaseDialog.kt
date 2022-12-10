package com.ahmedhenna.thepantry.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup.LayoutParams
import android.view.Window
import androidx.fragment.app.DialogFragment

open class BaseDialog(private val onDismiss: (()->Unit)? = null) : DialogFragment() {

    protected fun fixDialog() {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }


    override fun onPause() {
        super.onPause()
        dismissAllowingStateLoss()
    }
}