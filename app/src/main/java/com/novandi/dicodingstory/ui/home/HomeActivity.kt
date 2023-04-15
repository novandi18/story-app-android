package com.novandi.dicodingstory.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.databinding.ActivityHomeBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.detail.DetailActivity
import com.novandi.dicodingstory.ui.main.MainActivity
import com.novandi.dicodingstory.ui.story.StoryActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var loginModel: LoginModel
    private lateinit var mLoginPreference: LoginPreference
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        setupLayout()
        showResult()
        menuOptions()

        binding.btnCreateStory.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }
    }

    private fun setupLayout() {
        val layoutManager = LinearLayoutManager(this@HomeActivity)
        binding.rvStory.layoutManager = layoutManager
    }

    private fun showResult() {
        homeViewModel.getStories(loginModel.token.toString())
        homeViewModel.stories.observe(this@HomeActivity) { stories ->
            val homeAdapter = HomeAdapter(stories)
            binding.rvStory.adapter = homeAdapter

            homeAdapter.setOnItemClickListener(object : HomeAdapter.OnItemClickListener {
                override fun onItemClicked(data: StoryItems) {
                    val intent = Intent(this@HomeActivity, DetailActivity::class.java)
                    intent.putExtra(EXTRA_STORY_ID, data.id)
                    startActivity(intent)
                }
            })
        }

        homeViewModel.snackbarText.observe(this@HomeActivity) {
            it.getContentIfNotHandled()?.let { text ->
                if (text != "") Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        homeViewModel.isLoading.observe(this@HomeActivity) { showLoading(it) }
    }

    private fun menuOptions() {
        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_logout -> {
                    MaterialAlertDialogBuilder(this@HomeActivity)
                        .setTitle(getString(R.string.logout_title))
                        .setMessage(getString(R.string.logout_desc))
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(getString(R.string.logout_confirm)) { _, _ ->
                            mLoginPreference.clearUserLogin()
                            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                            finish()
                        }
                        .show()

                    true
                }
                else -> false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}