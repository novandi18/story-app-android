package com.novandi.dicodingstory.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.novandi.dicodingstory.databinding.ActivityMainBinding
import com.novandi.dicodingstory.ui.signup.SignupActivity
import com.novandi.dicodingstory.ui.story.StoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnSignin.setOnClickListener { startActivity(Intent(this, StoryActivity::class.java)) }
        binding.btnSigninToSignup.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }
}