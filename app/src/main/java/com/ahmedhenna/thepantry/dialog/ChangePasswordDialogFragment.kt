package com.ahmedhenna.thepantry.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ahmedhenna.thepantry.databinding.DialogChangePasswordBinding
import com.ahmedhenna.thepantry.databinding.DialogRecoverPasswordBinding

class ChangePasswordDialogFragment(private val onChangeClick: (oldPassword: String, newPassword: String) -> Unit) : BaseDialog() {

    private lateinit var binding: DialogChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fixDialog()
        binding = DialogChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val oldPassword = binding.editTextOldPassword.text.toString()
            val newPassword = binding.editTextNewPassword.text.toString()
            val newPasswordConfirm = binding.editTextConfirmPassword.text.toString()

            if(newPassword != newPasswordConfirm){
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onChangeClick(oldPassword, newPassword)
            dismiss()
        }
    }
}