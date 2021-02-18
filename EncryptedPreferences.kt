package com.kochartech.jetpacklibrariesexamples

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

@RequiresApi(Build.VERSION_CODES.M)
object EncryptedPreferences {

    private lateinit var encryptedSharedPreferences: SharedPreferences

    /**
     *  Use init method in Application class of Project for intialize
     *  encrypted shared preference instance
     *
     *  @param context Application Context
     *  @param preferencesFileName File name which will save encrypted key-value pairs saved
     */
    fun init(context: Context, preferencesFileName: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            preferencesFileName, masterKeyAlias, context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun putString(key: String?, value: String?) {
        val editor = encryptedSharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putInt(key: String?, value: Int) {
        val editor = encryptedSharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        val editor = encryptedSharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putFloat(key: String?, value: Float) {
        val editor = encryptedSharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun putLong(key: String?, value: Long) {
        val editor = encryptedSharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun putStringSet(key: String?, value: Set<String?>) {
        val editor = encryptedSharedPreferences.edit()
        editor.putStringSet(key, value)
        editor.apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return encryptedSharedPreferences.getString(key, defaultValue)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return encryptedSharedPreferences.getBoolean(key, defaultValue)
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return encryptedSharedPreferences.getFloat(key, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return encryptedSharedPreferences.getInt(key, defaultValue)
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return encryptedSharedPreferences.getLong(key, defaultValue)
    }

    fun getStringSet(key: String?, defaultValue: Set<String?>?): Set<String?>? {
        return encryptedSharedPreferences.getStringSet(key, defaultValue)
    }

}