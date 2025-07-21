package com.example.adminshopeease.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminshopeease.activity.AdminActivity
import com.example.adminshopeease.Model.Admins
import com.example.adminshopeease.R
import com.example.adminshopeease.objectClass.Utils
import com.example.adminshopeease.databinding.FragmentOTPBinding
import com.example.adminshopeease.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class fragment_o_t_p : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding : FragmentOTPBinding
    private lateinit var userNumber : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOTPBinding.inflate(inflater,container,false)
        getUserNum()
        customizingOtpEntering()
        sendOtp()
        binding.backfromotptosignin.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_o_t_p_to_fragment_sign_in)
        }
        binding.inputOtpB.setOnClickListener{
            onLoginButtonClicked()
        }
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.inputOtpB.setOnClickListener{
            Utils.showDialog(requireContext(),"Signing Now ...")
            val editTexts = arrayOf(binding.otp1,binding.otp2,binding.otp3,binding.otp4,binding.otp5,binding.otp6)
            val otp = editTexts.joinToString(""){ it.text.toString() }
            if(otp.length < editTexts.size) {
                Utils.showtoast(requireContext(),"Please enter right otp")
            }else{
                editTexts.forEach{it.text?.clear() ; it.clearFocus()}
                binding.inputOtpB.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.lime_green)
                verifyOtp(otp)
            }
        }
    }

    private fun getUserNum(){
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.numberText.text = userNumber
    }

    private fun customizingOtpEntering(){
        val otpText = arrayOf(binding.otp1,binding.otp2,binding.otp3,
            binding.otp4,binding.otp5,binding.otp6)

        for(indices in otpText.indices){
            otpText[indices].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 1) {
                        if (indices <= otpText.size - 1) {
                            if (indices + 1 <= otpText.size - 1) {
                                otpText[indices + 1].requestFocus()
                            }
                        } else if (s?.length == 0) {
                            if (indices > 0) {
                                otpText[indices - 1].requestFocus()
                            }

                        }
                    }
                }

            })
        }
    }
    private fun sendOtp(){
        Utils.showDialog(requireContext(),"Sending the OTP")
        viewModel.apply{
            sendOtp(userNumber , requireActivity())
            lifecycleScope.launch{
                otpSent.collect{it->
                    if(it == true){
                        Utils.shutDialog(requireContext())
                        Utils.showtoast(requireContext(),"Otp sent to the number ... ")
                    }
                }
            }
        }
    }
    private fun verifyOtp(otp : String) {
        val admins = Admins(uid = Utils.getCurrentUserid(), userPhnNumber = userNumber )
        viewModel.signInWithPhoneAuthCredential(otp,userNumber,admins,requireContext())

        lifecycleScope.launch{
            viewModel.isSignedSuccessfully.collect{
                if(it){
                    Utils.shutDialog(requireContext())
                    Utils.showtoast(requireContext(),"Loggged In...")
                    startActivity(Intent(requireActivity(), AdminActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }
}