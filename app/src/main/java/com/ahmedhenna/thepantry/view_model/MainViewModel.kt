package com.ahmedhenna.thepantry.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmedhenna.thepantry.common.Sort
import com.ahmedhenna.thepantry.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainViewModel : ViewModel() {

    private val originalItems = mutableListOf<GroceryItem>()
    private val _items = MutableLiveData<List<GroceryItem>>(listOf())
    val items: LiveData<List<GroceryItem>> = _items

    private val filteredItems = MutableLiveData<MutableList<GroceryItem>>(mutableListOf())


    private val _cartItems = MutableLiveData<List<GroceryCartItem>>(listOf())
    val cartItems: LiveData<List<GroceryCartItem>> = _cartItems

    private val currentSearch = MutableLiveData("")

    private val _currentSort = MutableLiveData(Sort.DEFAULT)
    val currentSort: LiveData<Sort> = _currentSort

    private val _currentCategory = MutableLiveData("All")
    val currentCategory: LiveData<String> = _currentCategory

    var currentQuantity = MutableLiveData(0)

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    init {
        populateList()
        setCartListener()
    }

    private fun populateList() {
        if (originalItems.isEmpty()) {
            db.collection("groceries").addSnapshotListener { value, error->
                if(error != null || value == null){
                    Log.e("Groceries Failed", error?.stackTraceToString() ?: "Unknown error")
                } else {
                    val items =
                        value.documents.mapNotNull { doc -> doc.toObject(GroceryItem::class.java) }
                    _items.value = items.shuffled().filter { it.status ===  Status.APPROVED}
                    originalItems.addAll(_items.value!!)
                    filteredItems.value =
                        mutableListOf<GroceryItem>().apply { addAll(_items.value!!) }
                }
            }
        }
    }

    private fun setCartListener() {
        db.collection("users").document(auth.uid!!).addSnapshotListener { value, error ->
            if (error == null) {
                _cartItems.value = value!!.toObject(UserItem::class.java)!!.cart
            }
        }
    }

    fun addCartItem(item: GroceryItem, quantity: Int = 1) {
        val list = _cartItems.value!!.toMutableList()
        var added = false
        for (oldItem in list) {
            if (oldItem.item.sku == item.sku) {
                oldItem.quantity = oldItem.quantity + quantity
                added = true
            }
        }

        if (!added) {
            list.add(GroceryCartItem(item, quantity))
        }
        db.collection("users").document(auth.uid!!).update(mapOf("cart" to list))

    }

    fun increaseQuantity(item: GroceryItem) {
        val list = _cartItems.value!!.toMutableList()
        for (oldItem in list.toList()) {
            if (oldItem.item.sku == item.sku) {
                list[list.indexOf(oldItem)] = oldItem.copy(quantity = oldItem.quantity + 1)
            }
        }
        db.collection("users").document(auth.uid!!).update(mapOf("cart" to list))
    }

    fun decreaseQuantity(item: GroceryItem) {
        val list = _cartItems.value!!.toMutableList()
        for (oldItem in list) {
            if (oldItem.item.sku == item.sku) {
                if (oldItem.quantity > 1) {
                    list[list.indexOf(oldItem)] = oldItem.copy(quantity = oldItem.quantity - 1)
                }
            }
        }
        db.collection("users").document(auth.uid!!).update(mapOf("cart" to list))
    }

    fun removeFromCart(item: GroceryItem) {
        val list = _cartItems.value!!.toMutableList()
        for (oldItem in list.toList()) {
            if (oldItem.item.sku == item.sku) {
                list.remove(oldItem)
                break
            }
        }
        db.collection("users").document(auth.uid!!).update(mapOf("cart" to list))
    }

    fun getItemForSku(sku: String): GroceryItem? {
        for (item in items.value!!) {
            if (sku == item.sku) {
                return item
            }
        }
        return null
    }

    fun submitOrder() {
        val order = OrderItem(_cartItems.value!!, Date(System.currentTimeMillis()))
        db.collection("users").document(auth.uid!!).update("orders", FieldValue.arrayUnion(order))
        db.collection("users").document(auth.uid!!)
            .update(mapOf("cart" to listOf<GroceryCartItem>()))
    }

    fun search(query: String) {
        currentSearch.value = query
        val list = filteredItems.value!!
        val filteredList = mutableListOf<GroceryItem>()

        if (query.isEmpty()) {
            filteredList.addAll(list)
        } else {
            for (item in list) {
                if (item.name.contains(query, ignoreCase = true)
                ) {
                    filteredList.add(item)
                }
            }
        }
        _items.value = filteredList
    }

    fun filter(category: String, sort: Sort) {
        _currentCategory.value = category
        _currentSort.value = sort
        val filtered = mutableListOf<GroceryItem>()
        when (category) {
            "All" -> {
                filtered.addAll(originalItems)
            }
            "Fruit" -> {
                originalItems.forEach { if (it.categories.contains("fruit")) filtered.add(it) }
            }
            "Vegetables" -> {
                originalItems.forEach { if (it.categories.contains("vegetable")) filtered.add(it) }
            }
            "Dairy" -> {
                originalItems.forEach { if (it.categories.contains("dairy")) filtered.add(it) }
            }
            "Meat" -> {
                originalItems.forEach { if (it.categories.contains("meat")) filtered.add(it) }
            }
            "Condiments" -> {
                originalItems.forEach { if (it.categories.contains("condiment")) filtered.add(it) }
            }
            "Snacks" -> {
                originalItems.forEach { if (it.categories.contains("snack")) filtered.add(it) }
            }
            "Grain" -> {
                originalItems.forEach { if (it.categories.contains("grain")) filtered.add(it) }
            }
            "Drinks" -> {
                originalItems.forEach { if (it.categories.contains("drink")) filtered.add(it) }
            }
        }

        when (sort) {
            Sort.DEFAULT -> {}
            Sort.LOW_TO_HIGH -> {
                filtered.sortBy { it.price }
            }
            Sort.HIGH_TO_LOW -> {
                filtered.sortByDescending { it.price }
            }
        }
        filteredItems.value = filtered
        search(currentSearch.value!!)
    }

    fun addItemAsDoctor(
        name: String,
        description: String,
        price: String,
        category: String,
        imageURL: String,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        val categories = category.split(",").map { it.trim().lowercase() }
        val currentUser = auth.currentUser!!
        val groceryItem = GroceryItem(
            name,
            description,
            generateSku(name),
            price.toDouble(),
            categories,
            imageURL,
            Status.PENDING,
            "${currentUser.displayName}"
        )
        db.collection("groceries").add(groceryItem).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun generateSku(name: String): String {
        val r = Random()
        val randomNumber = r.nextInt(1000 + 1)
        return name.lowercase().substring(0, name.length.coerceAtMost(3)) + randomNumber
    }
}