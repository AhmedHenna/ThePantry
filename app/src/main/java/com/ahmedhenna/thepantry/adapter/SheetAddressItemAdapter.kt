package com.ahmedhenna.thepantry.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedhenna.thepantry.adapter.diff.AddressItemDiffCallback
import com.ahmedhenna.thepantry.databinding.ViewAddressItemBinding
import com.ahmedhenna.thepantry.databinding.ViewAddressSheetBinding
import com.ahmedhenna.thepantry.model.AddressItem
import java.util.*


class SheetAddressItemAdapter(
    private val onItemSelect: (item: AddressItem) -> Unit
) :
    ListAdapter<AddressItem, SheetAddressItemAdapter.SheetAddressItemViewHolder>(AddressItemDiffCallback()) {

    class SheetAddressItemViewHolder(val binding: ViewAddressSheetBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<AddressItem>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetAddressItemViewHolder {
        return SheetAddressItemViewHolder(
            ViewAddressSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SheetAddressItemViewHolder, position: Int) {
        val item = currentList[position]
        val binding = holder.binding

        binding.addressName.text = item.name
        binding.addressInfo.text = "${item.area}, Floor ${item.floor}, Apartment ${item.apartment}"
        binding.phoneNumber.text = item.phone
        binding.root.setOnClickListener {
            onItemSelect(item)
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }

}

