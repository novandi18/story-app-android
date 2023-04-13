package com.novandi.dicodingstory.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.databinding.ActivityMainBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.home.HomeActivity
import com.novandi.dicodingstory.ui.signup.SignupActivity
import com.novandi.dicodingstory.utils.validateEmail
import com.novandi.dicodingstory.utils.validatePassword

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel // INI NEVER INITIALIZE
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        isLoggedIn()
        inputValidation()
        binding.btnSignin.setOnClickListener {
            checkUser()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.btnSigninToSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }

    private fun inputValidation() {
        binding.edLoginEmail.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validateEmail(this)
            binding.edLoginEmailLayout.error = if (validMessage != "") validMessage else null
        }

        binding.edLoginPassword.doOnTextChanged { text, _, _, _ ->
            val validMessage = text.validatePassword(this)
            binding.edLoginPasswordLayout.error = if (validMessage != "") validMessage else null
        }
    }

    private fun checkUser() {
        val email = binding.edLoginEmail.text
        val password = binding.edLoginPassword.text

        val validEmail = email.validateEmail(this)
        val validPassword = password.validatePassword(this)

        if (validEmail == "" && validPassword == "") {
            mainViewModel.isLoading.observe(this@MainActivity) { showLoading(it) }

            mainViewModel.userLogin(email.toString(), password.toString())
            mainViewModel.loginResult.observe(this) { result ->
                postUserLogin(result.userId, result.name, result.token)
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }

            mainViewModel.snackbarText.observe(this) {
                it.getContentIfNotHandled()?.let { text ->
                    Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.edLoginEmailLayout.error = if (validEmail != "") validEmail else null
            binding.edLoginPasswordLayout.error = if (validPassword != "") validPassword else null
        }
    }

    private fun postUserLogin(userId: String, name: String, token: String) {
        val loginPreferences = LoginPreference(this)

        loginModel.userId = userId
        loginModel.name = name
        loginModel.token = token
        loginModel.isLoggedIn = true

        loginPreferences.setUserLogin(loginModel)
    }

    private fun isLoggedIn() {
        if (mLoginPreference.getUserLogin().token != "" && mLoginPreference.getUserLogin().isLoggedIn) {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnSignin.isEnabled = !isLoading
        binding.edLoginEmailLayout.isEnabled = !isLoading
        binding.edLoginPasswordLayout.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
}