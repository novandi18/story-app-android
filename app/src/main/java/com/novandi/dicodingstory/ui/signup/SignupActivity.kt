package com.novandi.dicodingstory.ui.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.databinding.ActivitySignupBinding
import com.novandi.dicodingstory.utils.validateEmail
import com.novandi.dicodingstory.utils.validateName
import com.novandi.dicodingstory.utils.validatePassword

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputValidation()
        binding.btnSignup.setOnClickListener { checkInputValue() }
        binding.btnBackSignup.setOnClickListener { finish() }

        signupViewModel.isLoading.observe(this) { showLoading(it) }
        signupViewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputValidation() {
        binding.edRegisterName.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validateName(this)
            binding.edRegisterNameLayout.error = if (validMessage != "") validMessage else null
        }

        binding.edRegisterEmail.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validateEmail(this)
            binding.edRegisterEmailLayout.error = if (validMessage != "") validMessage else null
        }

        binding.edRegisterPassword.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validatePassword(this)
            binding.edRegisterPasswordLayout.error = if (validMessage != "") validMessage else null
        }
    }

    private fun checkInputValue() {
        val name = binding.edRegisterName.text
        val email = binding.edRegisterEmail.text
        val password = binding.edRegisterPassword.text

        val validName = name.validateName(this)
        val validEmail = email.validateEmail(this)
        val validPassword = password.validatePassword(this)

        if (validName == "" && validEmail == "" && validPassword == "") {
            signupViewModel.isLoading.observe(this@SignupActivity) { showLoading(it) }
            signupViewModel.userRegister(name.toString(), email.toString(), password.toString())
        } else {
            binding.edRegisterNameLayout.error = if (validName != "") validName else null
            binding.edRegisterEmailLayout.error = if (validEmail != "") validEmail else null
            binding.edRegisterPasswordLayout.error = if (validPassword != "") validPassword else null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnSignup.isEnabled = !isLoading
        binding.edRegisterNameLayout.isEnabled = !isLoading
        binding.edRegisterEmailLayout.isEnabled = !isLoading
        binding.edRegisterPasswordLayout.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}