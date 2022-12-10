package com.ahmedhenna.thepantry.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.ahmedhenna.thepantry.model.GroceryItem

class GroceryItemDiffCallback : DiffUtil.ItemCallback<GroceryItem>() {

    override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
        return oldItem.sku == newItem.sku
    }

    override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
        return oldItem.sku == newItem.sku && oldItem.categories == newItem.categories
                && oldItem.description == newItem.description && oldItem.categories == newItem.categories
                && oldItem.image == newItem.image && oldItem.price == newItem.price
    }
}