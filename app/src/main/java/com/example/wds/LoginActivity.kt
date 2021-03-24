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
                    binding.tilPass.error = it.message
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
                        binding.tilEmail.error = it.message
                    }
                }
        }
    }

    private fun inputGood(email: String, password: String): Boolean {
        var emailGood = true
        var passGood = true
        with(binding) {
            tilEmail.error =
                if (email.isEmpty()) {
                    emailGood = false
                    "Email required!"
                } else null
            tilPass.error =
                when {
                    password.isEmpty() -> {
                        passGood = false
                        "Password required!"
                    }
                    password.length < 6 -> {
                        passGood = false
                        "Password must be at least 6 characters long"
                    }
                    else -> null
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
                        if (task.isSuccessful) "Subscribed to notifications"
                        else "Failed to subscribe to notifications"
                    toast(this,msg)
                }
            prefs(this).edit().putBoolean("subscribed",true).apply()
            val notificationService = Intent(this, MyFirebaseMessagingService::class.java)
            startService(notificationService)
        }
    }
}