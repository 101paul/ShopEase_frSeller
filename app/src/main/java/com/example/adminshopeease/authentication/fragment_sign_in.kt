package com.example.adminshopeease.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.adminshopeease.R
import com.example.adminshopeease.objectClass.Utils
import com.example.adminshopeease.databinding.FragmentSignInBinding

class fragment_sign_in : Fragment() {
    private lateinit var binding : FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container ,false)
        getUserNumber()
        binding.phnB.setOnClickListener{
            onContinueButtonClick()
        }
        return binding.root
    }

    private fun onContinueButtonClick(){
        var number = binding.InputUserPhn.text.toString()
        if(number.isEmpty() || number.length != 10){
            Utils.showtoast(requireContext(),"Enter a valid phone number")
        }else{
            val bundle = Bundle()
            bundle.putString("number",number)
            findNavController().navigate(R.id.action_fragment_sign_in_to_fragment_o_t_p,bundle)
        }
    }
    private fun getUserNumber(){
        binding.InputUserPhn.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                number: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val len = number?.length
                if(len == 10){
                    binding.phnB.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.lime_green)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

}