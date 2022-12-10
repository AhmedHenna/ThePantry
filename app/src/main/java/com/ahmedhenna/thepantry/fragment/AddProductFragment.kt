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
import com.ahmedhenna.thepantry.common.imageURLRegex
import com.ahmedhenna.thepantry.databinding.FragmentAddItemBinding
import com.ahmedhenna.thepantry.view_model.MainViewModel

class AddProductFragment : LoadableFragment() {
    private lateinit var binding: FragmentAddItemBinding
    private lateinit var navController: NavController
    private val model: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
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

        binding.submitButton.setOnClickListener {
            if (hasEmptyFields()) {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!imageURLRegex.matches(binding.imageURLEditText.text.toString())){
                Toast.makeText(context, "Image url not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()
            model.addItemAsDoctor(
                binding.itemNameEditText.text.toString(),
                binding.itemDescriptionEditText.text.toString(),
                binding.itemPriceEditText.text.toString(),
                binding.itemCategoryEditText.text.toString(),
                binding.imageURLEditText.text.toString(),
                onComplete = {
                    hideLoading()
                    Toast.makeText(context, "Item added and pending review", Toast.LENGTH_SHORT).show()
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
        return binding.itemCategoryEditText.text.toString().isBlank()
                || binding.imageURLEditText.text.toString().isBlank()
                || binding.itemPriceEditText.text.toString().isBlank()
                || binding.itemNameEditText.text.toString().isBlank()
                || binding.itemDescriptionEditText.text.toString().isBlank()
    }


}
