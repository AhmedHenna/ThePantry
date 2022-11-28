package com.ahmedhenna.thepantry.model

data class GroceryItem(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val image: String = "",
)
