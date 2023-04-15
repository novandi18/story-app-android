package com.novandi.dicodingstory.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.databinding.ActivityDetailBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.home.HomeActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        showResult()

        binding.appBar.setNavigationOnClickListener { finish() }
    }

    private fun showResult() {
        val storyId = intent.getStringExtra(HomeActivity.EXTRA_STORY_ID)

        detailViewModel.getStory(storyId.toString(), loginModel.token.toString())
        detailViewModel.story.observe(this@DetailActivity) { story ->
            Glide.with(this@DetailActivity).load(story.photoUrl).into(binding.detailImage)
            binding.detailDate.text = getString(R.string.story_date, story.createdAt.withDateFormat())
            binding.detailName.text = story.name
            binding.detailDescription.text = story.description
        }

        detailViewModel.isLoading.observe(this@DetailActivity) { showLoading(it) }

        detailViewModel.snackbarText.observe(this@DetailActivity) {
            it.getContentIfNotHandled()?.let { text ->
                if (text != "") Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun String.withDateFormat(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}