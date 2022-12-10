package com.ahmedhenna.thepantry.model

data class GroceryItem(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val price: Double = 0.0,
    val categories: List<String> = listOf(),
    val image: String = "",
    val status: Status = Status.APPROVED,
    val addedBy: String? = null,
    val recommendedBy: List<String> = listOf()
)

enum class Status{
    PENDING,
    APPROVED
}
