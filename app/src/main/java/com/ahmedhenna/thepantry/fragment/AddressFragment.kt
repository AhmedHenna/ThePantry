package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmedhenna.thepantry.adapter.AddressItemAdapter
import com.ahmedhenna.thepantry.databinding.FragmentAllAddressesBinding
import com.ahmedhenna.thepantry.view_model.AuthViewModel
import com.ahmedhenna.thepantry.view_model.MainViewModel


class AddressFragment : LoadableFragment() {
    private lateinit var binding: FragmentAllAddressesBinding
    private lateinit var navController: NavController
    private lateinit var adapter: AddressItemAdapter

    private val model: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setClickListeners()
        setUpRecycler()
    }


    private fun setUpList(){
        authViewModel.getCurrentUser {
            adapter.submitList(it.addresses.toMutableList())
        }
    }

    private fun setUpRecycler(){
        adapter = AddressItemAdapter(onDeleteClick = {address->
            model.removeAddress(
                address,
                onComplete = {
                    hideLoading()
                    Toast.makeText(context, "Address deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    setUpList()
                },
                onFail = {
                    hideLoading()
                    Log.e("DELETE ADD FAIL", it)
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
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