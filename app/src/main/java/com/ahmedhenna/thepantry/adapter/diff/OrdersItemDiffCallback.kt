package com.ahmedhenna.thepantry.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.ahmedhenna.thepantry.model.AddressItem
import com.ahmedhenna.thepantry.model.GroceryCartItem
import com.ahmedhenna.thepantry.model.OrderItem

class OrdersItemDiffCallback : DiffUtil.ItemCallback<OrderItem>() {

    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem.orderDateTime == newItem.orderDateTime
    }

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem.orderedItems == newItem.orderedItems && oldItem.address == newItem.address
    }


}