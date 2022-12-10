package com.ahmedhenna.thepantry.model

data class UserItem(
    val firstName: String ="",
    val lastName: String = "",
    val email: String ="",
    val orders: List<OrderItem> = listOf(),
    val cart: List<GroceryCartItem> = listOf(),
    val doctor: Boolean = false
)