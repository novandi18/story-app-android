package com.novandi.dicodingstory.ui.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.novandi.dicodingstory.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}