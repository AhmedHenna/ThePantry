package com.ahmedhenna.thepantry.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.databinding.FragmentOnboardingBinding
import com.ahmedhenna.thepantry.view_model.AuthViewModel

class OnboardingFragment : Fragment() {
    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navController: NavController
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        initTransitions()
        setUpClickListeners()
        if (authViewModel.isSignedIn()) {
            navigateToTabs()

        }
    }

    private fun initTransitions() {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    private fun setUpClickListeners() {
        binding.continueButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val action =
            OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment()
        navController.navigate(action)
    }

    private fun navigateToTabs() {
        val action =
            OnboardingFragmentDirections.actionOnboardingFragmentToBottomNavigationFragment()
        navController.navigate(action)
    }

}
