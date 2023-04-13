package com.novandi.dicodingstory.storage

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

internal class LoginPreference(context: Context) {
    private val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()

    private val masterKey = MasterKey.Builder(context).setKeyGenParameterSpec(spec).build()

    private val preference: SharedPreferences = EncryptedSharedPreferences.create(
        context, PREFS_NAME, masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setUserLogin(value: LoginModel) {
        val editor = preference.edit()
        editor.putString(USER_ID, value.userId)
        editor.putString(NAME, value.name)
        editor.putString(TOKEN, value.token)
        editor.putBoolean(IS_LOGGED_IN, value.isLoggedIn)
        editor.apply()
    }

    fun getUserLogin(): LoginModel {
        val model = LoginModel()
        model.userId = preference.getString(USER_ID, "")
        model.name = preference.getString(NAME, "")
        model.token = preference.getString(TOKEN, "")
        model.isLoggedIn = preference.getBoolean(IS_LOGGED_IN, false)

        return model
    }

    fun clearUserLogin() {
        val editor = preference.edit()
        editor.clear()
        editor.remove(USER_ID)
        editor.remove(NAME)
        editor.remove(TOKEN)
        editor.remove(IS_LOGGED_IN)
        editor.apply()
    }

    companion object {
        const val PREFS_NAME = "login_pref"
        const val USER_ID = "id"
        const val NAME = "name"
        const val TOKEN = "token"
        const val IS_LOGGED_IN = "isloggedin"
    }
}