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

    val currentSearch = MutableLiveData("")

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
            db.collection("groceries").addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    Log.e("Groceries Failed", error?.stackTraceToString() ?: "Unknown error")
                } else {
                    val items =
                        value.documents.mapNotNull { doc -> doc.toObject(GroceryItem::class.java) }
                    _items.value = items.shuffled().filter { it.status === Status.APPROVED }
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

    fun populateCart(cartItems: List<GroceryCartItem>) {
        val list = _cartItems.value!!.toMutableList()

        for(cartItem in cartItems){
            var added = false
            for (oldItem in list) {
                if (oldItem.item.sku == cartItem.item.sku) {
                    oldItem.quantity = oldItem.quantity + cartItem.quantity
                    added = true
                }
            }
            if (!added) {
                list.add(GroceryCartItem(cartItem.item, cartItem.quantity))
            }
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

    fun submitOrder(address: AddressItem) {
        val order = OrderItem(_cartItems.value!!, Date(System.currentTimeMillis()), address)
        db.collection("users").document(auth.uid!!).update("orders", FieldValue.arrayUnion(order))
        db.collection("users").document(auth.uid!!)
            .update(mapOf("cart" to listOf<GroceryCartItem>()))
    }

    fun addAddress(
        address: AddressItem,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        db.collection("users").document(auth.uid!!)
            .update("addresses", FieldValue.arrayUnion(address)).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun removeAddress(
        address: AddressItem,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        db.collection("users").document(auth.uid!!)
            .update("addresses", FieldValue.arrayRemove(address)).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
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
            else -> {
                originalItems.forEach { if (it.categories.contains(category.lowercase())) filtered.add(it) }
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
                categories.forEach {cat->
                    addCategory(cat, currentUser.displayName!!)
                }
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun addCategory(
        category: String,
        addedBy: String
    ) {
        db.collection("categories").add(Category(category, addedBy))
    }

    fun getCategories(onComplete: (List<Category>)->Unit) {
        db.collection("categories").get().addOnCompleteListener {
            if(it.isSuccessful && it.result != null){
                onComplete(it.result.toObjects(Category::class.java)!!)
            }
        }
    }

    private fun generateSku(name: String): String {
        val r = Random()
        val randomNumber = r.nextInt(1000 + 1)
        return name.lowercase().substring(0, name.length.coerceAtMost(3)) + randomNumber
    }

    fun addRecommendAsDoctor(
        sku: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit
    ) {
        val currentUser = auth.currentUser!!
        db.collection("groceries").whereEqualTo("sku", sku).limit(1).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.documents[0].reference.update(
                    "recommendedBy",
                    FieldValue.arrayUnion(currentUser.displayName)
                ).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        onComplete()
                    } else {
                        onFail(updateTask.exception?.localizedMessage ?: "Unknown error")
                    }
                }
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun removeRecommendAsDoctor(
        sku: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit
    ) {
        val currentUser = auth.currentUser!!
        db.collection("groceries").whereEqualTo("sku", sku).limit(1).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.documents[0].reference.update(
                    "recommendedBy",
                    FieldValue.arrayRemove(currentUser.displayName)
                ).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        onComplete()
                    } else {
                        onFail(updateTask.exception?.localizedMessage ?: "Unknown error")
                    }
                }
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }
}