package com.kobo.san

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class MyApplication : Application() {

    companion object {
        private const val PREFS_NAME = "com.kobo.san.prefs"

        private const val KEY_CUSTOM_SUFFIX = "customSuffix"
        private const val KEY_CUSTOM_STRING = "customString"
        private const val KEY_CUSTOM_DOMAIN = "customDomain"
        private const val KEY_CUSTOM_ADDRES = "customAddress"

        private var prefs: SharedPreferences? = null

        var customSuffix: String
            get() = prefs?.getString(KEY_CUSTOM_SUFFIX, "KOBO") ?: "KOBO"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_SUFFIX, value)?.apply()
            }

        var customString: String
            get() = prefs?.getString(KEY_CUSTOM_STRING, "warja123@") ?: "warja123@"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_STRING, value)?.apply()
            }

        var customDomain: String
            get() = prefs?.getString(KEY_CUSTOM_DOMAIN, "@hotmail.com") ?: "@hotmail.com"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_DOMAIN, value)?.apply()
            }
        var customAddress: String
            get() = prefs?.getString(KEY_CUSTOM_ADDRES, "CODE") ?: "CODE"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_ADDRES, value)?.apply()
            }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "Floating Widget Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}


