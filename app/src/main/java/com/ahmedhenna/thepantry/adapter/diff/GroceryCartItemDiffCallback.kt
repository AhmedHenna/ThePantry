package com.ahmedhenna.thepantry.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.ahmedhenna.thepantry.model.GroceryCartItem

class GroceryCartItemDiffCallback : DiffUtil.ItemCallback<GroceryCartItem>() {

    override fun areItemsTheSame(oldItem: GroceryCartItem, newItem: GroceryCartItem): Boolean {
        return oldItem.item.sku == newItem.item.sku
    }

    override fun areContentsTheSame(oldItem: GroceryCartItem, newItem: GroceryCartItem): Boolean {
        return oldItem.quantity == newItem.quantity
    }


}