package com.ahmedhenna.thepantry.model

data class UserItem(
    val name: String ="",
    val email: String ="",
    val orders: List<OrderItem> = listOf(),
    val cart: List<GroceryCartItem> = listOf(),
    val isDoctor: Boolean = false
)