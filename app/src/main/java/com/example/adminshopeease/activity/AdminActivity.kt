package com.example.adminshopeease.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.adminshopeease.R
import com.example.adminshopeease.databinding.ActivityadminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityadminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityadminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NavigationUI.setupWithNavController(binding.bnavigation,
            Navigation.findNavController(this, R.id.fragmentContainerView2))


    }
}