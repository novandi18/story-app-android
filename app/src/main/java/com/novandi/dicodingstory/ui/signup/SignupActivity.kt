package com.novandi.dicodingstory.ui.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.databinding.ActivitySignupBinding
import com.novandi.dicodingstory.utils.validateEmail
import com.novandi.dicodingstory.utils.validatePassword

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputValidation()
        binding.btnBackSignup.setOnClickListener { finish() }
    }

    private fun inputValidation() {
        binding.edRegisterName.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) binding.edRegisterName.error = getString(R.string.error_name_empty)
        }

        binding.edRegisterEmail.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validateEmail()
            if (validMessage != "") binding.edRegisterEmail.error = validMessage
        }

        binding.edRegisterPassword.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validatePassword()
            if (validMessage != "") binding.edRegisterPassword.error = validMessage
        }
    }
}