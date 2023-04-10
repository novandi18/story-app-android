package com.novandi.dicodingstory.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.novandi.dicodingstory.databinding.ActivityMainBinding
import com.novandi.dicodingstory.ui.home.HomeActivity
import com.novandi.dicodingstory.ui.signup.SignupActivity
import com.novandi.dicodingstory.utils.validateEmail
import com.novandi.dicodingstory.utils.validatePassword

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputValidation()
        binding.btnSignin.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        binding.btnSigninToSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }

    private fun inputValidation() {
        binding.edLoginEmail.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validateEmail()
            if (validMessage != "") binding.edLoginEmail.error = validMessage
        }

        binding.edLoginPassword.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validatePassword()
            if (validMessage != "") binding.edLoginPassword.error = validMessage
        }
    }
}