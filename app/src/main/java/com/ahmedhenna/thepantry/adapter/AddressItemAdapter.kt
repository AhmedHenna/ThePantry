package com.ahmedhenna.thepantry.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedhenna.thepantry.adapter.diff.AddressItemDiffCallback
import com.ahmedhenna.thepantry.adapter.diff.OrdersItemDiffCallback
import com.ahmedhenna.thepantry.common.toBitmap
import com.ahmedhenna.thepantry.databinding.ViewAddressItemBinding
import com.ahmedhenna.thepantry.databinding.ViewOrderDetailsBinding
import com.ahmedhenna.thepantry.model.AddressItem
import com.ahmedhenna.thepantry.model.GroceryCartItem
import com.ahmedhenna.thepantry.model.OrderItem
import java.text.SimpleDateFormat
import java.util.*


class AddressItemAdapter(
    private val onDeleteClick: (item: AddressItem) -> Unit
) :
    ListAdapter<AddressItem, AddressItemAdapter.AddressItemViewHolder>(AddressItemDiffCallback()) {

    class AddressItemViewHolder(val binding: ViewAddressItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<AddressItem>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressItemViewHolder {
        return AddressItemViewHolder(
            ViewAddressItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AddressItemViewHolder, position: Int) {
        val item = currentList[position]
        val binding = holder.binding

        binding.addressName.text = item.name
        binding.addressInfo.text = "${item.area}, Floor ${item.floor}, Apartment ${item.apartment}"
        binding.phoneNumber.text = item.phone
        binding.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }

}

