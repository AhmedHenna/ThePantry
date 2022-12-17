package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ahmedhenna.thepantry.databinding.FragmentAddAddressBinding
import com.ahmedhenna.thepantry.model.AddressItem
import com.ahmedhenna.thepantry.view_model.MainViewModel

class AddressAddFragment : LoadableFragment() {
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var navController: NavController
    private val model: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setClickListeners()
    }


    private fun setClickListeners() {
        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            if (hasEmptyFields()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            showLoading()
            val address = AddressItem(
                binding.addressNameEditText.text.toString(),
                binding.areaEditText.text.toString(),
                binding.addressDetailsEditText.text.toString(),
                binding.floorNumberEditText.text.toString(),
                binding.apartmentNumberEditText.text.toString(),
                binding.phoneNumberEditText.text.toString()
            )

            model.addAddress(
                address,
                onComplete = {
                    hideLoading()
                    Toast.makeText(context, "Address added successfully", Toast.LENGTH_SHORT)
                        .show()
                    navController.popBackStack()
                },
                onFail = {
                    hideLoading()
                    Log.e("ADD FAIL", it)
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun hasEmptyFields(): Boolean {
        return binding.addressNameEditText.text.toString().isBlank()
                || binding.apartmentNumberEditText.toString().isBlank()
                || binding.addressDetailsEditText.text.toString().isBlank()
                || binding.areaEditText.text.toString().isBlank()
                || binding.phoneNumberEditText.text.toString().isBlank()
                || binding.floorNumberEditText.text.toString().isBlank()

    }


}
