package com.novandi.dicodingstory.storage

import android.content.Context

internal class SettingsPreference(context: Context) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setSettings(value: SettingsModel) {
        val editor = preferences.edit()
        editor.putString(LANGUAGE, value.language)
        editor.apply()
    }

    fun getSettings(): SettingsModel {
        val model = SettingsModel()
        model.language = preferences.getString(LANGUAGE, "id")
        return model
    }

    companion object {
        private const val PREF_NAME = "settings_pref"
        private const val LANGUAGE = "language"
    }
}