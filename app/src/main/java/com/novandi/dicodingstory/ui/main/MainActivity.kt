package com.novandi.dicodingstory.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.databinding.ActivityMainBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.home.HomeActivity
import com.novandi.dicodingstory.ui.signup.SignupActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
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

        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { inputValidation() }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { inputValidation() }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnSigninToSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }

    private fun inputValidation() {
        val emailField = binding.edLoginEmail
        val passwordField = binding.edLoginPassword
        binding.btnSignin.isEnabled = (emailField.error == null && emailField.text.toString().isNotEmpty())
                && (passwordField.error == null && passwordField.text.toString().isNotEmpty())
    }

    private fun checkUser() {
        val emailValue = binding.edLoginEmail.text.toString()
        val passwordValue = binding.edLoginPassword.text.toString()

        mainViewModel.isLoading.observe(this@MainActivity) { showLoading(it) }

        mainViewModel.userLogin(emailValue, passwordValue)
        mainViewModel.loginResult.observe(this) { result ->
            postUserLogin(result.userId, result.name, result.token)
            postUserLogin(result.userId, result.name, result.token)
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }

        mainViewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
            }
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
        with(binding) {
            btnSignin.isEnabled = !isLoading
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }
    }
}