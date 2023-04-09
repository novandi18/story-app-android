package com.novandi.dicodingstory.ui.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.novandi.dicodingstory.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackSignup.setOnClickListener { finish() }
    }
}