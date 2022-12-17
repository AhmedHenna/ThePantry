package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.transition.TransitionInflater
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.adapter.GroceryItemsAdapter
import com.ahmedhenna.thepantry.common.Sort
import com.ahmedhenna.thepantry.common.hideKeyboard
import com.ahmedhenna.thepantry.databinding.FragmentHomeBinding
import com.ahmedhenna.thepantry.dialog.FilterDialogFragment
import com.ahmedhenna.thepantry.view_model.AuthViewModel
import com.ahmedhenna.thepantry.view_model.MainViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentNavController: NavController
    private lateinit var adapter: GroceryItemsAdapter

    private val model: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var layoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        parentNavController = Navigation.findNavController(requireActivity(), R.id.mainNav)
        setUpRecycler()
        setObservers()

        binding.searchBarEditText.addTextChangedListener {
            model.search(it.toString())
        }

        binding.filterButton.setOnClickListener {
            val filterDialogFragment = FilterDialogFragment(model)
            filterDialogFragment.show(childFragmentManager, "FILTER")
        }
        sharedElementEnterTransition =
            context?.let {
                TransitionInflater.from(it).inflateTransition(android.R.transition.move)
            }

        authViewModel.getCurrentUser {
            binding.userName.text = "${it.firstName} ${it.lastName}"
            if (it.doctor) {
                binding.plusImage.visibility = View.VISIBLE
                binding.plusImage.setOnClickListener {
                    navigateToAddProduct()
                }
            } else {
                binding.plusImage.visibility = View.GONE
            }
        }

    }

    private fun setUpRecycler() {
        adapter = GroceryItemsAdapter(requireContext()) { item, trans ->
            navigateToItemDetails(item.sku, trans)

        }
        binding.recyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun setObservers() {
        model.items.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
            }
            adapter.submitList(it.toMutableList())
            Handler(Looper.getMainLooper()).postDelayed({
                val smoothScroller = LinearSmoothScroller(requireContext())
                smoothScroller.targetPosition = 0
                layoutManager.startSmoothScroll(smoothScroller)
            }, 200)


        }

        model.currentSort.observe(viewLifecycleOwner) {
            if (model.currentSort.value != Sort.DEFAULT || model.currentCategory.value != "All") {
                binding.filterButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_green
                    )
                )
            } else {
                binding.filterButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
        }
        model.currentCategory.observe(viewLifecycleOwner) {
            if (model.currentSort.value != Sort.DEFAULT || model.currentCategory.value != "All") {
                binding.filterButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_green
                    )
                )
            } else {
                binding.filterButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
            }
        }

        model.currentSearch.observe(viewLifecycleOwner){
            if(it != binding.searchBarEditText.text.toString()){
                binding.searchBarEditText.setText(it)
            }
        }

    }

    private fun navigateToItemDetails(sku: String, image: View) {
        ViewCompat.setTransitionName(image, "image_${sku}")
        val extras = FragmentNavigatorExtras(image to "image_$sku")

        val action =
            BottomNavigationFragmentDirections.actionBottomNavigationFragmentToItemDetailsFragment(
                sku
            )
        parentNavController.navigate(action, extras)
    }

    private fun navigateToAddProduct() {
        val action =
            BottomNavigationFragmentDirections.actionBottomNavigationFragmentToAddProductFragment()
        parentNavController.navigate(action)
    }


}