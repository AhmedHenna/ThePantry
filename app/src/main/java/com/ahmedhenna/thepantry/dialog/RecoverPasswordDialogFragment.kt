package com.ahmedhenna.thepantry.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmedhenna.thepantry.databinding.DialogRecoverPasswordBinding

class RecoverPasswordDialogFragment(private val onRecoverClick: (email: String) -> Unit) : BaseDialog() {

    private lateinit var binding: DialogRecoverPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fixDialog()
        binding = DialogRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recoverButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            onRecoverClick(email)
            dismiss()
        }
    }
}