package com.ahmedhenna.thepantry.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedhenna.thepantry.adapter.diff.OrdersItemDiffCallback
import com.ahmedhenna.thepantry.common.toBitmap
import com.ahmedhenna.thepantry.databinding.ViewOrderDetailsBinding
import com.ahmedhenna.thepantry.model.GroceryCartItem
import com.ahmedhenna.thepantry.model.OrderItem
import java.text.SimpleDateFormat
import java.util.*


class OrdersItemAdapter(
    private val context: Context,
    private val onAddToCartClick: (items: List<GroceryCartItem>) -> Unit
) :
    ListAdapter<OrderItem, OrdersItemAdapter.OrdersItemViewHolder>(OrdersItemDiffCallback()) {

    class OrdersItemViewHolder(val binding: ViewOrderDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<OrderItem>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersItemViewHolder {
        return OrdersItemViewHolder(
            ViewOrderDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrdersItemViewHolder, position: Int) {
        val item = currentList[position]
        val binding = holder.binding

        val datePattern = "EEE, d MMM yyyy HH:mm:ss"
        val dateFormat = SimpleDateFormat(datePattern, Locale.getDefault())

        binding.orderNumber.text = "Order #${position + 1}"
        binding.orderTime.text = dateFormat.format(item.orderDateTime)

        val total = item.orderedItems.sumOf { groceryCartItem ->
            groceryCartItem.item.price * groceryCartItem.quantity
        }
        binding.orderPrice.text = "EGP ${String.format("%.2f", total)}"


        val count = item.orderedItems.sumOf { groceryCartItem ->
            groceryCartItem.quantity
        }
        binding.itemQuantity.text = "$count items"



        item.orderedItems.getOrNull(0)?.item?.image?.toBitmap(
            context,
            binding.firstImage::setImageBitmap
        )

        item.orderedItems.getOrNull(1)?.item?.image?.toBitmap(
            context,
            binding.secondImage::setImageBitmap
        )

        item.orderedItems.getOrNull(2)?.item?.image?.toBitmap(
            context,
            binding.thirdImage::setImageBitmap
        )

        binding.addButton.setOnClickListener {
            onAddToCartClick(item.orderedItems)
        }


    }

    override fun getItemCount(): Int {
        return currentList.size
    }

}

