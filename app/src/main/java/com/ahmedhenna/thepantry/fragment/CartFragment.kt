package com.ahmedhenna.thepantry.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmedhenna.thepantry.adapter.GroceryCartItemsAdapter
import com.ahmedhenna.thepantry.databinding.FragmentCartBinding
import com.ahmedhenna.thepantry.dialog.ConfirmationDialogFragment
import com.ahmedhenna.thepantry.view_model.MainViewModel

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var navController: NavController
    private lateinit var adapter: GroceryCartItemsAdapter

    private val model: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setUpRecycler()
        setObservers()

        binding.checkoutButton.setOnClickListener {
            val confirmationDialogFragment = ConfirmationDialogFragment {}
            confirmationDialogFragment.show(childFragmentManager, "CONFIRM")
            model.submitOrder()
        }
    }

    private fun setUpRecycler() {
        adapter = GroceryCartItemsAdapter(
            requireContext(),
            onDeleteClick = { model.removeFromCart(it.item) },
            onMinusClick = { model.decreaseQuantity(it.item) },
            onPlusClick = { model.increaseQuantity(it.item) })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers() {
        model.cartItems.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())

            var totalCost = 0.0
            var totalItems = 0
            for (item in it) {
                totalCost += item.quantity * item.item.price
                totalItems += item.quantity
            }
            binding.numberOfItems.text = "($totalItems items)"
            binding.total.text = "\$${String.format("%.2f", totalCost)}"

            if (it.isEmpty()) {
                binding.total.visibility = View.GONE
                binding.checkoutButton.isEnabled = false
            } else {
                binding.total.visibility = View.VISIBLE
                binding.checkoutButton.isEnabled = true
            }
        }
    }


}