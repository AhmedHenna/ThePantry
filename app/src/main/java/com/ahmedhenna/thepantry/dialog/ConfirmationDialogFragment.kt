package com.ahmedhenna.thepantry.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmedhenna.thepantry.databinding.DialogOrderConfirmationBinding

class ConfirmationDialogFragment(onDismiss: () -> Unit) : BaseDialog(onDismiss) {

    private lateinit var binding: DialogOrderConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fixDialog()
        binding = DialogOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dismissButton.setOnClickListener {
            dismiss()
        }
    }

}