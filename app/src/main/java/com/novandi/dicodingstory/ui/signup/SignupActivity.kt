package com.novandi.dicodingstory.ui.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputValidation()
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { inputValidation() }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { inputValidation() }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { inputValidation() }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnSignup.setOnClickListener { registerUser() }
        binding.btnBackSignup.setOnClickListener { finish() }

        signupViewModel.isLoading.observe(this) { showLoading(it) }
        signupViewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputValidation() {
        val nameField = binding.edRegisterName
        val emailField = binding.edRegisterEmail
        val passwordField = binding.edRegisterPassword
        binding.btnSignup.isEnabled = (nameField.error == null && nameField.text.toString().isNotEmpty()) &&
                (emailField.error == null && emailField.text.toString().isNotEmpty()) &&
                (passwordField.error == null && passwordField.text.toString().isNotEmpty())
    }

    private fun registerUser() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        signupViewModel.isLoading.observe(this@SignupActivity) { showLoading(it) }
        signupViewModel.userRegister(name, email, password)
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            btnSignup.isEnabled = !isLoading
            edRegisterName.isEnabled = !isLoading
            edRegisterEmail.isEnabled = !isLoading
            edRegisterPassword.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }
    }
}