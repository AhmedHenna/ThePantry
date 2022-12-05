package com.ahmedhenna.thepantry.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.databinding.FragmentBottomNavigationBinding

class BottomNavigationFragment : Fragment() {
    private lateinit var binding: FragmentBottomNavigationBinding
    private var tabNavController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nestedNavHostFragment = childFragmentManager.fragments[0] as? NavHostFragment
        tabNavController = nestedNavHostFragment?.findNavController()
        setClickListeners()
        setSelectedColor(R.id.homeFragment)
    }

    override fun onResume() {
        super.onResume()
        resetBar()
        tabNavController?.currentDestination?.id?.let {
            setSelectedItem(it)
        }
    }

    private fun setClickListeners() {
        binding.tabBar.home.setOnClickListener {
            setSelectedItem(R.id.homeFragment)
        }
        binding.tabBar.cart.setOnClickListener {
            setSelectedItem(R.id.cartFragment)
        }
        binding.tabBar.account.setOnClickListener {
            setSelectedItem(R.id.accountFragment)
        }
    }

    private fun setSelectedItem(@IdRes selected: Int) {
        tabNavController!!.navigate(selected)
        resetBar()
        setSelectedColor(selected)
    }

    private fun resetBar() {
        val tabBar = binding.tabBar

        val a7Color = Color.parseColor("#A7A7A7")
        val f5Color = Color.parseColor("#F5F5F5")


        tabBar.homeIcon.setColorFilter(a7Color)
        DrawableCompat.setTint(
            DrawableCompat.wrap(tabBar.circleBgHome.background),
            f5Color
        )
        tabBar.homeText.setTextColor(a7Color)

        tabBar.cartIcon.setColorFilter(a7Color)
        DrawableCompat.setTint(
            DrawableCompat.wrap(tabBar.circleBgCart.background),
            f5Color
        )
        tabBar.cartText.setTextColor(a7Color)

        tabBar.accountIcon.setColorFilter(a7Color)
        DrawableCompat.setTint(
            DrawableCompat.wrap(tabBar.circleBgAccount.background),
            f5Color
        )
        tabBar.accountText.setTextColor(a7Color)
    }

    private fun setSelectedColor(@IdRes selected: Int) {
        val tabBar = binding.tabBar
        val green = ContextCompat.getColor(requireContext(), R.color.green)

        when (selected) {
            R.id.homeFragment -> {
                tabBar.homeIcon.setColorFilter(Color.WHITE)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(tabBar.circleBgHome.background),
                    green
                )
                tabBar.homeText.setTextColor(green)
            }
            R.id.cartFragment -> {
                tabBar.cartIcon.setColorFilter(Color.WHITE)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(tabBar.circleBgCart.background),
                    green
                )
                tabBar.cartText.setTextColor(green)
            }
            R.id.accountFragment -> {
                tabBar.accountIcon.setColorFilter(Color.WHITE)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(tabBar.circleBgAccount.background),
                    green
                )
                tabBar.accountText.setTextColor(green)
            }
        }
    }


}