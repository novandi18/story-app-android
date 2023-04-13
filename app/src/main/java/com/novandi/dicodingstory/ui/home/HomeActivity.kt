package com.novandi.dicodingstory.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.novandi.dicodingstory.R
import com.novandi.dicodingstory.databinding.ActivityHomeBinding
import com.novandi.dicodingstory.storage.LoginModel
import com.novandi.dicodingstory.storage.LoginPreference
import com.novandi.dicodingstory.ui.main.MainActivity
import com.novandi.dicodingstory.ui.story.StoryActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var loginModel: LoginModel
    private lateinit var mLoginPreference: LoginPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)
        loginModel = mLoginPreference.getUserLogin()

        binding.btnCreateStory.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }

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
}