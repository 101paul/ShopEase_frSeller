package com.example.adminshopeease.objectClass

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.example.adminshopeease.R
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import kotlin.toString

object Utils {
    private lateinit var dialog : Dialog
    fun showtoast(context : Context ,msg : String ){
        Toast.makeText(context , msg , Toast.LENGTH_LONG).show()
    }
    fun showDialog(context : Context,msg : String){
        dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_box1)
        var ShowMsg1 = dialog.findViewById<TextView>(R.id.dialogT1)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT

        )
        ShowMsg1.text = msg
        dialog.show()
    }
    fun shutDialog(context : Context){
        dialog.dismiss()
    }

    private var firebaseAuthInstance : FirebaseAuth?= null
    fun getAuthInstance() : FirebaseAuth{
        if(firebaseAuthInstance == null ){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }
    fun getCurrentUserid() : String {
        return FirebaseAuth.getInstance().currentUser?.uid.toString()
    }
    fun getRandomId() : String {
        return UUID.randomUUID().toString()
    }

}