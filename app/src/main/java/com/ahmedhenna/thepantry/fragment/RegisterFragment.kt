package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.databinding.FragmentRegisterBinding

class RegisterFragment : AuthFragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initTransitions()
        setClickListeners()
        if (authViewModel.isSignedIn()) {
            navigateToTabs()

        }
    }

    private fun initTransitions() {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    private fun setClickListeners() {
        binding.signInHyperLink.setOnClickListener {
            navigateToLogin()
        }
        binding.registerButton.setOnClickListener {
            if (binding.emailEditText.text.isNullOrBlank() || binding.passwordEditText.text.isNullOrBlank() || binding.firstNameEditText.text.isNullOrBlank() || binding.lastNameEditText.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showLoading()
            authViewModel.registerEmailPassword(
                binding.emailEditText.text.toString(),
                binding.firstNameEditText.text.toString(),
                binding.lastNameEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                onComplete = {
                    navigateToTabs()
                    hideLoading()
                },
                onFail = {
                    Log.e("FAIL REGISTER", it)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    hideLoading()
                })
        }

        binding.googleButton.setOnClickListener {
            showLoading()
            initiateGoogleAuth(
                onComplete = {
                    navigateToTabs()
                    hideLoading()
                },
                onFail = {
                    Log.e("FAIL GOOGLE", it)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    hideLoading()
                })
        }
    }


    private fun navigateToLogin() {
        val action =
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navController.navigate(action)
    }

    private fun navigateToTabs() {
        val action =
            RegisterFragmentDirections.actionRegisterFragmentToBottomNavigationFragment()
        navController.navigate(action)
    }

}