package com.example.adminshopeease.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminshopeease.activity.AdminActivity
import com.example.adminshopeease.R
import com.example.adminshopeease.viewModel.AuthViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class fragment_splash : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            // Fetch and save FCM token (fire-and-forget)

            val isLoggedIn = withContext(Dispatchers.IO) {
                viewModel.CurrentUser.first()
            }

            delay(2000) // Wait 2 seconds before navigating

            if (isAdded) {
                if (isLoggedIn) {
                    startActivity(Intent(requireActivity(), AdminActivity::class.java))
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    requireActivity().finish()
                } else {
                    if (findNavController().currentDestination?.id == R.id.fragment_splash) {
                        findNavController().navigate(R.id.action_fragment_splash_to_fragment_sign_in)
                    }
                }
            }
        }
        return view
    }
}
