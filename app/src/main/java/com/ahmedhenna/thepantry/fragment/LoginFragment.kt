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
import com.ahmedhenna.thepantry.databinding.FragmentLoginBinding
import com.ahmedhenna.thepantry.dialog.RecoverPasswordDialogFragment
import com.ahmedhenna.thepantry.dialog.ResetConfirmDialogFragment

class LoginFragment : AuthFragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
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
        binding.signUpHyperLink.setOnClickListener {
            navigateToRegister()
        }
        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.isNullOrBlank() || binding.passwordEditText.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showLoading()
            authViewModel.signInEmailPassword(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                onComplete = {
                    navigateToTabs()
                    hideLoading()
                },
                onFail = {
                    Log.e("FAIL LOG IN", it)
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

        binding.recoverPassword.setOnClickListener {
            val recoverPasswordDialog = RecoverPasswordDialogFragment(onRecoverClick = { email ->
                showLoading()
                authViewModel.sendResetPasswordEmail(
                    email,
                    onComplete = {
                        hideLoading()
                        val resetConfirmDialogFragment = ResetConfirmDialogFragment {}
                        resetConfirmDialogFragment.show(childFragmentManager, "RESET")
                    },
                    onFail = {
                        hideLoading()
                        Log.e("FAIL SEND RESET", it)
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    })
            })
            recoverPasswordDialog.show(childFragmentManager, "RECOVER")
        }
    }

    private fun navigateToRegister() {
        val action =
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navController.navigate(action)
    }

    private fun navigateToTabs() {
        val action =
            LoginFragmentDirections.actionLoginFragmentToBottomNavigationFragment()
        navController.navigate(action)
    }
}
