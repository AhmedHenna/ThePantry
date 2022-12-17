package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.adapter.SheetAddressItemAdapter
import com.ahmedhenna.thepantry.databinding.BottomSheetAddressesBinding
import com.ahmedhenna.thepantry.model.AddressItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddressSelectionSheetFragment(
    private val addresses: List<AddressItem>,
    private val onAddressSelected: (address: AddressItem) -> Unit
) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddressesBinding
    private lateinit var parentNavController: NavController
    private lateinit var adapter: SheetAddressItemAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentNavController = Navigation.findNavController(requireActivity(), R.id.mainNav)

        setUpRecycler()

        binding.addButton.setOnClickListener {
            dismiss()
            val action = BottomNavigationFragmentDirections.actionBottomNavigationFragmentToAddressAddFragment()
            parentNavController.navigate(action)
        }
    }

    private fun setUpRecycler() {
        adapter = SheetAddressItemAdapter(onItemSelect = {
            dismiss()
            onAddressSelected(it)
        })
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        adapter.submitList(addresses.toMutableList())
    }

    override fun onPause() {
        super.onPause()
        dismissAllowingStateLoss()
    }
}