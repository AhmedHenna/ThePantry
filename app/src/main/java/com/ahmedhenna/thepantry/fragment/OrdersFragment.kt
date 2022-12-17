package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmedhenna.thepantry.adapter.OrdersItemAdapter
import com.ahmedhenna.thepantry.databinding.FragmentOrderHistoryBinding
import com.ahmedhenna.thepantry.view_model.AuthViewModel
import com.ahmedhenna.thepantry.view_model.MainViewModel


class OrdersFragment : LoadableFragment() {
    private lateinit var binding: FragmentOrderHistoryBinding
    private lateinit var navController: NavController
    private lateinit var adapter: OrdersItemAdapter

    private val model: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setClickListeners()
        setUpRecycler()
    }


    private fun setUpList() {
        authViewModel.getCurrentUser {
            adapter.submitList(it.orders.toMutableList())
        }
    }

    private fun setUpRecycler() {
        adapter = OrdersItemAdapter(requireContext(), onAddToCartClick = {
          model.populateCart(it)
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        setUpList()
    }

    private fun setClickListeners() {
        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }
    }
}