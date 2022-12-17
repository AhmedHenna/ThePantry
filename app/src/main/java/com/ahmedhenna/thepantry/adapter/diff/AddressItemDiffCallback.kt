package com.ahmedhenna.thepantry.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.ahmedhenna.thepantry.model.AddressItem
import com.ahmedhenna.thepantry.model.GroceryCartItem

class AddressItemDiffCallback : DiffUtil.ItemCallback<AddressItem>() {

    override fun areItemsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return oldItem.area == newItem.area && oldItem.apartment == newItem.area && oldItem.floor == newItem.floor &&
                oldItem.details == newItem.details && oldItem.phone == newItem.phone
    }


}