package com.kobo.san

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {
        const val PREF_NAME = "com.kobo.san.PREFERENCE_FILE_KEY"
        const val CUSTOM_SUFFIX = "customSuffix"
        const val CUSTOM_STRING = "customString"
        const val CUSTOM_DOMAIN = "customDomain"
        const val CUSTOM_ADDRES = "customAddress"
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var customSuffix: String?
        get() = sharedPref.getString(CUSTOM_SUFFIX, "KOBO")
        set(value) = sharedPref.edit().putString(CUSTOM_SUFFIX, value).apply()

    var customString: String?
        get() = sharedPref.getString(CUSTOM_STRING, "warja123@")
        set(value) = sharedPref.edit().putString(CUSTOM_STRING, value).apply()

    var customDomain: String?
        get() = sharedPref.getString(CUSTOM_DOMAIN, "@hotmail.com")
        set(value) = sharedPref.edit().putString(CUSTOM_DOMAIN, value).apply()

    var customAddress: String?
        get() = sharedPref.getString(CUSTOM_ADDRES, "CODE")
        set(value) = sharedPref.edit().putString(CUSTOM_ADDRES, value).apply()
}
