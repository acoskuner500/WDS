package com.example.wds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wds.databinding.ActivityLoginBinding
import com.example.wds.utilities.MyFirebaseMessagingService
import com.example.wds.utilities.prefs
import com.example.wds.utilities.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            loginButton.setOnClickListener {
                login(etEmail.text.toString(), etPass.text.toString())
            }
            createUserButton.setOnClickListener {
                createUser(etEmail.text.toString(), etPass.text.toString())
            }
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startMain()
        }
    }

    private fun login(email: String, password: String) {
        if (inputGood(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startMain()
                }
                .addOnFailureListener {
                    binding.etPass.apply{
                        error = it.message!!
                        requestFocus()
                    }
                }
        }
    }

    private fun createUser(email: String, password: String) {
        if (inputGood(email, password)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    startMain()
                }
                .addOnFailureListener {
                    binding.etEmail.apply{
                        error = it.message!!
                        requestFocus()
                    }
                }
        }
    }

    private fun inputGood(email: String, password: String): Boolean {
        var emailGood = true
        var passGood = true
        with(binding) {
            if (email.isEmpty()) {
                etEmail.apply {
                    error = "Email required"
                    requestFocus()
                }
                emailGood = false
            }
            if (password.isEmpty()) {
                etPass.apply {
                    error = "Password required"
                    requestFocus()
                }
                passGood = false
            }
            if (password.length < 6) {
                etPass.apply {
                    error = "Password must be at least 6 characters long"
                    requestFocus()
                }
                passGood = false
            }
        }
        return emailGood && passGood
    }

    private fun startMain() {
        notificationSetup()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun notificationSetup() {
        val subscribed = prefs(this).getBoolean("subscribed",false)
        if (!subscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic("new-deterred")
                .addOnCompleteListener { task ->
                    val msg =
                        if (task.isSuccessful) "Successfully subscribed to notifications"
                        else "Failed to subscribe to notifications"
                    toast(this,msg)
                }
            prefs(this).edit().putBoolean("subscribed",true).apply()
            val notificationService = Intent(this, MyFirebaseMessagingService::class.java)
            startService(notificationService)
        }
    }
}