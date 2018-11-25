package com.marknjunge.marlin.data.local

import android.content.Context
import android.preference.PreferenceManager
import com.marknjunge.marlin.data.model.AccessToken
import com.squareup.moshi.Moshi

interface PreferencesStorage{
    var accessToken: AccessToken?
}

class PreferencesStorageImpl(context: Context):PreferencesStorage {
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val moshi = Moshi.Builder().build()
    private val accessTokenAdapter by lazy { moshi.adapter(AccessToken::class.java) }

    private val ACCESS_TOKEN_KEY = "access_token_key"

    override var accessToken: AccessToken?
        set(value) {
            val json = if (value == null) {
                ""
            } else {
                accessTokenAdapter.toJson(value)
            }
            sharedPreferences.edit()
                    .putString(ACCESS_TOKEN_KEY, json)
                    .apply()
        }
        get() {
            val json = sharedPreferences.getString(ACCESS_TOKEN_KEY, "")
            return if (json == "") {
                null
            } else {
                accessTokenAdapter.fromJson(json)
            }
        }
}
