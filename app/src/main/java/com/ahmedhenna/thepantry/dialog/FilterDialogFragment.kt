package com.ahmedhenna.thepantry.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.common.Sort
import com.ahmedhenna.thepantry.common.capitalizeWords
import com.ahmedhenna.thepantry.databinding.DialogFilterBinding
import com.ahmedhenna.thepantry.view_model.MainViewModel

class FilterDialogFragment(private val model: MainViewModel) : BaseDialog() {

    private lateinit var binding: DialogFilterBinding

    private var category = model.currentCategory.value!!
    private var sort = model.currentSort.value!!
    private var currentCategories = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fixDialog()
        binding = DialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitValues()
        updateOnSelection()

        model.getCategories { categories ->
            setUpDropdown(categories.map { it.category.capitalizeWords() })
        }

        binding.dismissButton.setOnClickListener {
            dismiss()
        }
        binding.applyButton.setOnClickListener {
            model.filter(category, sort)
            dismiss()
        }
    }

    private fun setUpDropdown(categories: List<String>) {
        currentCategories = listOf("All", *categories.toTypedArray())
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, currentCategories)
        binding.categoriesAutoComplete.setAdapter(adapter)
    }

    private fun setInitValues() {
        val currentSort = model.currentSort.value

        binding.radioGroup.check(
            when (currentSort) {
                Sort.DEFAULT -> R.id.radio_button_0
                Sort.LOW_TO_HIGH -> R.id.radio_button_1
                Sort.HIGH_TO_LOW -> R.id.radio_button_2
                else -> R.id.radio_button_0
            }
        )
        binding.categoriesAutoComplete.setText(model.currentCategory.value!!)
    }

    private fun updateOnSelection() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_0 -> {
                    sort = Sort.DEFAULT
                }
                R.id.radio_button_1 -> {
                    sort = Sort.LOW_TO_HIGH
                }
                R.id.radio_button_2 -> {
                    sort = Sort.HIGH_TO_LOW
                }
            }
        }

        binding.categoriesAutoComplete.setOnItemClickListener { _, _, position, _ ->
            category = when (position) {
                0 -> "All"
                else -> currentCategories[position]
            }
        }
    }

}