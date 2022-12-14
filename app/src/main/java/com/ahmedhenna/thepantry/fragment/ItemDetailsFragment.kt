package com.ahmedhenna.thepantry.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.transition.TransitionInflater
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.common.capitalizeWords
import com.ahmedhenna.thepantry.common.px
import com.ahmedhenna.thepantry.common.toBitmap
import com.ahmedhenna.thepantry.databinding.FragmentItemDetailsBinding
import com.ahmedhenna.thepantry.view_model.AuthViewModel
import com.ahmedhenna.thepantry.view_model.MainViewModel


class ItemDetailsFragment : LoadableFragment() {
    private lateinit var binding: FragmentItemDetailsBinding
    private lateinit var navController: NavController

    private val args by navArgs<ItemDetailsFragmentArgs>()
    private val model: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentQuantity.value = 1
        navController = findNavController()
        setUpViews()
        setClickListeners()
        setUpObservers()
        ViewCompat.setTransitionName(binding.itemImage, "image_${args.sku}")
        sharedElementEnterTransition =
            context?.let {
                TransitionInflater.from(it).inflateTransition(android.R.transition.move)
            }


    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun setUpViews() {
        val sku = args.sku
        val item = model.getItemForSku(sku) ?: return

        binding.itemName.text = item.name.capitalizeWords()
        binding.itemDescription.text = item.description


        val context = requireContext()

        item.image.toBitmap(context) { bitmap ->
            binding.itemImage.setImageBitmap(bitmap)

            Palette.Builder(bitmap).generate {
                val green = ContextCompat.getColor(context, R.color.green)
                it?.let { palette ->
                    var lightColor =
                        palette.getLightVibrantColor(green)

                    if (lightColor == green) {
                        lightColor = palette.getDominantColor(green)
                    }

                    val drawable: GradientDrawable =
                        binding.itemFrame.background as GradientDrawable
                    drawable.setStroke(2.px, lightColor)
                    binding.colorBackground.setBackgroundColor(lightColor)

                }
            }
        }

        if (item.recommendedBy.isEmpty()) {
            binding.recommendedBy.visibility = View.GONE
        } else {
            val recommendedList = item.recommendedBy.take(item.recommendedBy.size.coerceAtMost(2))
            var recommendedString = recommendedList.joinToString(", ")

            if(item.recommendedBy.size - 2 > 0){
                recommendedString += "+ ${item.recommendedBy.size - 2} more"
            }
            binding.recommendedNames.text = recommendedString
        }

        binding.itemCategories.text = item.categories.joinToString(", ")

        authViewModel.getCurrentUser {
            if (it.doctor) {
                binding.plusImage.visibility = View.GONE
                binding.minusImage.visibility = View.GONE
                binding.numberOfItems.visibility = View.GONE

                val isRecommended = item.recommendedBy.contains("${it.firstName} ${it.lastName}")
                setUpRecommended(isRecommended)
            }
        }


    }

    private fun setUpRecommended(recommended: Boolean) {
        if (recommended) {
            binding.addButton.text = "Remove recommendation"
            binding.addButton.setOnClickListener {
                showLoading()
                model.removeRecommendAsDoctor(args.sku,
                    onComplete = {
                        hideLoading()
                        setUpRecommended(false)
                    },
                    onFail = {
                        hideLoading()
                        Log.e("REMOVE REC FAIL", it)
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
            }
        } else {
            binding.addButton.text = "Recommend"
            binding.addButton.setOnClickListener {
                showLoading()
                model.addRecommendAsDoctor(args.sku,
                    onComplete = {
                        hideLoading()
                        setUpRecommended(true)
                    },
                    onFail = {
                        hideLoading()
                        Log.e("REMOVE REC FAIL", it)
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }

    private fun setClickListeners() {
        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }
        binding.plusImage.setOnClickListener {
            model.currentQuantity.value = model.currentQuantity.value!! + 1
        }
        binding.minusImage.setOnClickListener {
            if (model.currentQuantity.value!! > 1) {
                model.currentQuantity.value = model.currentQuantity.value!! - 1
            }
        }
        binding.addButton.setOnClickListener {
            val item = model.getItemForSku(args.sku)
            if (item != null) {
                model.addCartItem(item, quantity = model.currentQuantity.value!!)
            }
            navController.popBackStack()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setUpObservers() {
        model.currentQuantity.observe(viewLifecycleOwner) {

            val item = model.getItemForSku(args.sku)
            val newPrice = (item?.price ?: 0.0) * it

            binding.itemPrice.text = "\$${String.format("%.2f", newPrice)}"
            binding.numberOfItems.text = it.toString()
            if (it == 1) {
                binding.minusImage.setColorFilter(Color.parseColor("#A7A7A7"))
            } else {
                binding.minusImage.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onDestroy() {
        super.onDestroy()
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.dark_green)
    }


}