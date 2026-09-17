package com.kvoxkobo.san

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class AppGlobals : Application() {

    companion object {
        private const val PREFS_NAME = "com.kvoxkobo.san.prefs"
        private const val KEY_CUSTOM_SUFFIX = "customSuffix"
        private const val KEY_CUSTOM_STRING = "customString"
        private const val KEY_CUSTOM_DOMAIN = "customDomain"
        private const val KEY_CUSTOM_ADDRESS = "customAddress"
        private const val KEY_CUSTOM_HTTPS = "customHttps"
        private const val KEY_CUSTOM_LINK1 = "customLink1"
        private const val KEY_CUSTOM_LINK2 = "customLink2"
        private const val KEY_CUSTOM_LINK3 = "customLink3"
        private const val KEY_CUSTOM_LINK4 = "customLink4"

        private var prefs: SharedPreferences? = null

        var customSuffix: String
            get() = prefs?.getString(KEY_CUSTOM_SUFFIX, "JOKOWI") ?: "JOKOWI"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_SUFFIX, value)?.apply()
            }

        var customString: String
            get() = prefs?.getString(KEY_CUSTOM_STRING, "@JOKOWI1122") ?: "@JOKOWI1122"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_STRING, value)?.apply()
            }

        var customDomain: String
            get() = prefs?.getString(KEY_CUSTOM_DOMAIN, "@hotmail.com") ?: "@hotmail.com"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_DOMAIN, value)?.apply()
            }

        var customAddress: String
            get() = prefs?.getString(KEY_CUSTOM_ADDRESS, "CODE") ?: "CODE"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_ADDRESS, value)?.apply()
            }

        // Tambahkan di companion object, setelah customDomain:

        var customHttps: String
            get() = prefs?.getString(KEY_CUSTOM_HTTPS, "https://generator.email/") ?: "https://generator.email/"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_HTTPS, value)?.apply()
            }

        var customLink1: String
            get() = prefs?.getString(KEY_CUSTOM_LINK1, "https://vt.tokopedia.com/t/ZS9N5WUGDvtnb-x0ZJu/") ?: "https://vt.tokopedia.com/t/ZS9N5WUGDvtnb-x0ZJu/"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_LINK1, value)?.apply()
            }

        var customLink2: String
            get() = prefs?.getString(KEY_CUSTOM_LINK2, "https://vt.tokopedia.com/t/ZS9N5GPcj4arG-N3pZ0/") ?: "https://vt.tokopedia.com/t/ZS9N5GPcj4arG-N3pZ0/"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_LINK2, value)?.apply()
            }

        var customLink3: String
            get() = prefs?.getString(KEY_CUSTOM_LINK3, "https://vt.tokopedia.com/t/ZS9N57RYJKGRR-yfzz2/") ?: "https://vt.tokopedia.com/t/ZS9N57RYJKGRR-yfzz2/"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_LINK3, value)?.apply()
            }

        var customLink4: String
            get() = prefs?.getString(KEY_CUSTOM_LINK4, "https://vt.tokopedia.com/t/ZS9N5wctQa5da-0AncD/") ?: "https://vt.tokopedia.com/t/ZS9N5wctQa5da-0AncD/"
            set(value) {
                prefs?.edit()?.putString(KEY_CUSTOM_LINK4, value)?.apply()
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

