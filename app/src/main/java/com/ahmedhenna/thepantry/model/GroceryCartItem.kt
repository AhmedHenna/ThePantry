package com.ahmedhenna.thepantry.model

data class GroceryCartItem(
    val item: GroceryItem = GroceryItem(),
    var quantity: Int = 0,
)
