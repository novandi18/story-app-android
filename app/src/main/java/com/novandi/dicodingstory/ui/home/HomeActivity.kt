package com.novandi.dicodingstory.ui.home

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.novandi.dicodingstory.ui.map.MapsActivity
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
import com.novandi.dicodingstory.utils.getLanguages
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel
    private lateinit var mSettingsPreference: SettingsPreference
    private lateinit var settingsModel: SettingsModel
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(this)
    }

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
        val homeAdapter = HomeAdapter()
        binding.rvStory.adapter = homeAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                homeAdapter.retry()
            }
        )

        homeViewModel.stories.observe(this@HomeActivity) { stories ->
            homeAdapter.submitData(lifecycle, stories)
            homeAdapter.setOnItemClickListener(object : HomeAdapter.OnItemClickListener {
                override fun onItemClicked(data: StoryItems) {
                    val intent = Intent(this@HomeActivity, DetailActivity::class.java)
                    intent.putExtra(EXTRA_STORY_ID, data.id)
                    startActivity(intent)
                }
            })
        }
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
                            .setSingleChoiceItems(getLanguages()[0], getLanguages()[1].indexOf(settingsModel.language)) { dialog, selection ->
                                when (selection) {
                                    0 -> setLanguage(getLanguages()[1][0])
                                    1 -> setLanguage(getLanguages()[1][1])
                                }
                                recreate()
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                R.id.btn_maps -> {
                    startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
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

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}