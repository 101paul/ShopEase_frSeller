package com.example.adminshopeease.viewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminshopeease.Model.Admins
import com.example.adminshopeease.objectClass.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent
    private val _isSignedSuccessfully = MutableStateFlow(false)
    val isSignedSuccessfully = _isSignedSuccessfully
    private val _CurrentUser = MutableStateFlow(false)
    val CurrentUser = _CurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _CurrentUser.value = true
        }
    }

    fun sendOtp(userNumber: String, activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {

            val callbacks = object : PhoneAuthProvider
            .OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(e: FirebaseException) {

                    // Show a message and update the UI

                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    _verificationId.value = verificationId
                    _otpSent.value = true

                    // Save verification ID and resending token so we can use them later

                }
            }
            val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
                .setPhoneNumber("+91$userNumber") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }


    fun signInWithPhoneAuthCredential(
        otp: String,
        userNumber: String,
        admins: Admins,
        context: Context
    ) {
//        val verificationId = _verificationId.value
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)


            if (_verificationId.value.isNullOrEmpty()) {
                // Handle case where verificationId is not set (OTP might not have been sent)
                Utils.showDialog(context, "Please request OTP first.")
                return
            }
            viewModelScope.launch(Dispatchers.IO) {// this viewModelScope It automatically gets canceled when the ViewModel is cleared (e.g., when the associated Activity or Fragment is destroyed).
                try {
                    // Sign in with the credential
                    Utils.getAuthInstance().signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo")
                                    .child(admins.uid!!).setValue(admins)

                                _isSignedSuccessfully.value = true
                            } else {
                                // Handle sign-in failure
                                Utils.showDialog(context, "Error: ${task.exception?.message}")
                            }
                        }
                } catch (e: Exception) {
                    viewModelScope.launch(Dispatchers.Main) {

                        // Handle unexpected errors
                        Utils.showDialog(context, "Error: ${e.message}")
                    }
                }
            }
        }
}