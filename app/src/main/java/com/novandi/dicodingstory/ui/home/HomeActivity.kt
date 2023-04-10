package com.novandi.dicodingstory.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.novandi.dicodingstory.databinding.ActivityHomeBinding
import com.novandi.dicodingstory.ui.story.StoryActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateStory.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }
    }
}