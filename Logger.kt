package com.example.app

import android.util.Log
import com.example.app.BuildConfig

object Logger {

    private const val APP_TAG = "APP NAME : "
    private var isEnabled : Boolean = BuildConfig.DEBUG

    fun v(tag : String, message : String) {
        if (isEnabled) {
            Log.v(APP_TAG + tag, message)
        }
    }

    fun v(tag : String, message : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.v(APP_TAG + tag, message, throwable)
        }
    }

    fun d(tag : String, message : String) {
        if (isEnabled) {
            Log.d(APP_TAG + tag, message)
        }
    }

    fun d(tag : String, message : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.d(APP_TAG + tag, message, throwable)
        }
    }

    fun i(tag : String, message : String) {
        if (isEnabled) {
            Log.i(APP_TAG + tag, message)
        }
    }

    fun i(tag : String, message : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.i(APP_TAG + tag, message, throwable)
        }
    }

    fun w(tag : String, message : String) {
        if (isEnabled) {
            Log.w(APP_TAG + tag, message)
        }
    }

    fun w(tag : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.w(APP_TAG + tag, throwable)
        }
    }

    fun w(tag : String, message : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.w(APP_TAG + tag, message, throwable)
        }
    }

    fun e(tag : String, message : String) {
        if (isEnabled) {
            Log.e(APP_TAG + tag, message)
        }
    }

    fun e(tag : String, message : String, throwable: Throwable?) {
        if (isEnabled) {
            Log.e(APP_TAG + tag, message, throwable)
        }
    }
}