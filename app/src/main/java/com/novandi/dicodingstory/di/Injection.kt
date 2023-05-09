package com.novandi.dicodingstory.di

import android.content.Context
import com.novandi.dicodingstory.api.ApiConfig
import com.novandi.dicodingstory.data.StoryRepository
import com.novandi.dicodingstory.storage.LoginPreference

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val token = LoginPreference(context).getUserLogin().token.toString()
        return StoryRepository(apiService, "Bearer $token")
    }
}