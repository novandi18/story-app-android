package com.novandi.dicodingstory.ui.home

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.databinding.ActivityHomeBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.storage.SettingsModel
import com.novandi.dicodingstory.storage.SettingsPreference
import com.novandi.dicodingstory.ui.detail.DetailActivity
import com.novandi.dicodingstory.ui.main.MainActivity
import com.novandi.dicodingstory.ui.story.StoryActivity
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
    private lateinit var mSettingsPreference: SettingsPreference
    private lateinit var settingsModel: SettingsModel
    private val homeViewModel by viewModels<HomeViewModel>()
    private val languages = arrayOf("Bahasa Indonesia", "English")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettingsPreference = SettingsPreference(this)
        settingsModel = mSettingsPreference.getSettings()
        if (Build.VERSION.SDK_INT >= 24) setLanguage(settingsModel.language.toString())
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        if (intent.getStringExtra(StoryActivity.EXTRA_MESSAGE) != null) {
            Snackbar.make(window.decorView.rootView, intent.getStringExtra(StoryActivity.EXTRA_MESSAGE).toString(), Snackbar.LENGTH_SHORT).show()
        }

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
                R.id.btn_language -> {
                    if (Build.VERSION.SDK_INT <= 24) {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    } else {
                        MaterialAlertDialogBuilder(this@HomeActivity)
                            .setTitle(getString(R.string.language_title))
                            .setSingleChoiceItems(languages, -1) { dialog, selection ->
                                when (selection) {
                                    0 -> setLanguage("id")
                                    1 -> setLanguage("en")
                                }
                                recreate()
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                else -> false
            }
        }
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLanguage(language: String) {
        val localeSet = LocaleList(Locale(language))
        LocaleList.setDefault(localeSet)
        resources.configuration.setLocales(localeSet)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)

        settingsModel.language = language
        mSettingsPreference.setSettings(settingsModel)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}