package com.ahmedhenna.thepantry.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.adapter.diff.GroceryItemDiffCallback
import com.ahmedhenna.thepantry.common.capitalizeWords
import com.ahmedhenna.thepantry.common.px
import com.ahmedhenna.thepantry.common.toBitmap
import com.ahmedhenna.thepantry.databinding.ViewItemShopBinding
import com.ahmedhenna.thepantry.model.GroceryItem


class GroceryItemsAdapter(
    private val context: Context,
    private val onItemClick: (item: GroceryItem, transitionView: View) -> Unit
) :
    ListAdapter<GroceryItem, GroceryItemsAdapter.GroceryItemViewHolder>(GroceryItemDiffCallback()) {

    class GroceryItemViewHolder(val binding: ViewItemShopBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun submitList(list: MutableList<GroceryItem>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        return GroceryItemViewHolder(
            ViewItemShopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroceryItemViewHolder, position: Int) {
        val item = currentList[position]
        val binding = holder.binding


        binding.productIcon.setImageResource(android.R.color.transparent)
        binding.root.setOnClickListener {
            onItemClick(item, binding.productIcon)
        }

        binding.itemName.text = item.name.capitalizeWords()
        binding.itemPrice.text = "\$${String.format("%.2f", item.price)}"

        item.image.toBitmap(context) { bitmap ->
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

