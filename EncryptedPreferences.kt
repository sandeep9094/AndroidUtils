package com.example.jetpacklibrariesexamples

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptSharedPrefs(context: Context) {

    companion object {
        private const val SHARED_PREFS_NAME = "secret_shared_prefs"
        private const val IS_USER_FIRST_TIME = "isUserFirstTime"
    }

    private var preferences: SharedPreferences

    init {
        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val masterKeyAlias = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            "${context.packageName}_$SHARED_PREFS_NAME",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // use the shared preferences and editor as you normally would
        preferences = sharedPreferences
    }


    var isUserFirstTime: Boolean
        get() = preferences.getBoolean(IS_USER_FIRST_TIME, true)
        set(value) = preferences.edit().putBoolean(IS_USER_FIRST_TIME, value).apply()


}
