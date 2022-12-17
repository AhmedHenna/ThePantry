package com.ahmedhenna.thepantry.model

import java.util.*

data class OrderItem(
    val orderedItems: List<GroceryCartItem> = listOf(),
    val orderDateTime: Date = Date(),
    val address: AddressItem = AddressItem()
)
