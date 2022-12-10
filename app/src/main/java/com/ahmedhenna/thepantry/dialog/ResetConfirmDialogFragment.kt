package com.ahmedhenna.thepantry.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmedhenna.thepantry.databinding.DialogResetConfirmBinding

class ResetConfirmDialogFragment(onDismiss: () -> Unit) : BaseDialog(onDismiss) {

    private lateinit var binding: DialogResetConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fixDialog()
        binding = DialogResetConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dismissButton.setOnClickListener {
            dismiss()
        }
    }
}