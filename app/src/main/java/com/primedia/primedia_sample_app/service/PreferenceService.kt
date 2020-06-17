package com.primedia.primedia_sample_app.service

import android.content.Context
import android.content.SharedPreferences
import com.primedia.primedia_sample_app.ServerInfo
import com.primedia.primedia_sample_app.util.PreferencesConstants

class PreferenceService(appContext: Context) {

    private val preferenceManager: SharedPreferences by lazy {
        appContext.getSharedPreferences("com.primedia.primedia_sample_app.service", Context.MODE_PRIVATE)
    }

    private fun setString(key: String, value: String) {
        preferenceManager
            .edit()
            .putString(key, value)
            .apply()
    }

    private fun getString(key: String, fallback: String = ""): String {
        return preferenceManager.getString(key, fallback) ?: fallback
    }

    private fun setLong(key: String, value: Long) {
        preferenceManager
            .edit()
            .putLong(key, value)
            .apply()
    }

    private fun getLong(key: String, fallback: Long = 0L): Long {
        return preferenceManager.getLong(key, fallback)
    }

    private fun setInt(key: String, value: Int) {
        preferenceManager
            .edit()
            .putInt(key, value)
            .apply()
    }

    private fun getInt(key: String, fallback: Int = 0): Int {
        return preferenceManager.getInt(key, fallback)
    }

    private fun setBoolean(key: String, value: Boolean) {
        preferenceManager
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun getBoolean(key: String): Boolean {
        return preferenceManager.getBoolean(key, false)
    }

    private fun removePreference(key: String) {
        preferenceManager
            .edit()
            .remove(key)
            .apply()
    }


    ///////////

    var token: String
        set(value) = setString("token", value)
        get() = getString("token")

    var timeTokenSet: Long
        set(value) = setLong(PreferencesConstants.LAST_TOKEN_REFRESH.value, value)
        get() = getLong(PreferencesConstants.LAST_TOKEN_REFRESH.value)

    var lastEditedName: String
        set(value) = setString(PreferencesConstants.LAST_EDITED_NAME.value, value)
        get() = getString(PreferencesConstants.LAST_EDITED_NAME.value)

    var currentPlaylistUid: Int
        set(value) = setInt(PreferencesConstants.CURRENT_PLAYLIST_UID.value, value)
        get() = getInt(PreferencesConstants.CURRENT_PLAYLIST_UID.value)

    var userSignedIn: Boolean
        set(value) = setBoolean(PreferencesConstants.USER_SIGNED_IN.value, value)
        get() = getBoolean(PreferencesConstants.USER_SIGNED_IN.value)

    var userSetPreferences: Boolean
        set(value) = setBoolean(PreferencesConstants.USER_SET_PREFERENCES.value, value)
        get() = getBoolean(PreferencesConstants.USER_SET_PREFERENCES.value)

    var baseUrl: String
        set(value) = setString(PreferencesConstants.BASE_URL.value, value)
        get() = getString(PreferencesConstants.BASE_URL.value, ServerInfo.BASE_PRIMEDIA_REST_URL)

    var developerOptions: Boolean
        set(value) = setBoolean(PreferencesConstants.DEVELOPER_OPTIONS.value, value)
        get() = getBoolean(PreferencesConstants.DEVELOPER_OPTIONS.value)

    ///////////

    fun clearPreferncesOnSignout() {
        for (pref in PreferencesConstants.values()) {
            removePreference(PreferencesConstants.TOKEN.value)
        }

    }
}