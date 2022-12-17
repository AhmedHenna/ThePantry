package com.ahmedhenna.thepantry.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.adapter.diff.GroceryCartItemDiffCallback
import com.ahmedhenna.thepantry.common.capitalizeWords
import com.ahmedhenna.thepantry.common.getDrawableIdentifierFromString
import com.ahmedhenna.thepantry.common.px
import com.ahmedhenna.thepantry.common.toBitmap
import com.ahmedhenna.thepantry.databinding.ViewItemCartBinding
import com.ahmedhenna.thepantry.model.GroceryCartItem


class GroceryCartItemsAdapter(
    private val context: Context,
    private val onPlusClick: (item: GroceryCartItem) -> Unit,
    private val onMinusClick: (item: GroceryCartItem) -> Unit,
    private val onDeleteClick: (item: GroceryCartItem) -> Unit
) :
    ListAdapter<GroceryCartItem, GroceryCartItemsAdapter.CartItemViewHolder>(
        GroceryCartItemDiffCallback()
    ) {

    class CartItemViewHolder(val binding: ViewItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<GroceryCartItem>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            ViewItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = currentList[position]
        val cartItem = item.item
        val binding = holder.binding

        binding.productIcon.setImageResource(android.R.color.transparent)

        binding.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }

        binding.plusImage.setOnClickListener {
            onPlusClick(item)
        }

        binding.minusImage.setOnClickListener {
            onMinusClick(item)
        }

        if (item.quantity == 1) {
            binding.minusImage.setColorFilter(Color.parseColor("#A7A7A7"))
        } else {
            binding.minusImage.setColorFilter(
                ContextCompat.getColor(
                    context, R.color.red
                )
            )
        }

        binding.itemName.text = cartItem.name.capitalizeWords()
        binding.itemPrice.text = "\$${String.format("%.2f", cartItem.price * item.quantity)}"
        binding.numberOfItems.text = item.quantity.toString()

        cartItem.image.toBitmap(context) { bitmap ->
            binding.productIcon.setImageBitmap(bitmap)

            Palette.Builder(bitmap).generate {
                val green = ContextCompat.getColor(context, R.color.green)
                it?.let { palette ->
                    var lightColor =
                        palette.getLightVibrantColor(green)

                    if (lightColor == green) {
                        lightColor = palette.getDominantColor(green)
                    }

                    val drawable: GradientDrawable =
                        binding.iconFrame.background as GradientDrawable
                    drawable.setStroke(2.px, lightColor)

                }
            }
        }
    }


    override fun getItemCount(): Int {
        return currentList.size
    }

}

