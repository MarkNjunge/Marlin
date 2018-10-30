package com.marknjunge.marlin.data.local

import android.content.Context
import android.preference.PreferenceManager
import com.marknjunge.marlin.data.models.AccessToken
import com.marknjunge.marlin.data.models.User
import com.squareup.moshi.Moshi

interface PreferencesStorage{
    var accessToken: AccessToken?
    var user: User?
}

class PreferencesStorageImpl(context: Context):PreferencesStorage {
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    private val moshi = Moshi.Builder().build()
    private val accessTokenAdapter by lazy { moshi.adapter(AccessToken::class.java) }
    private val userAdapter by lazy { moshi.adapter(User::class.java) }

    private val ACCESS_TOKEN_KEY = "access_token_key"
    private val USER_KEY = "user"

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

    override var user: User?
        set(value) {
            val json = if (value == null) {
                ""
            } else {
                userAdapter.toJson(value)
            }
            sharedPreferences.edit()
                    .putString(USER_KEY, json)
                    .apply()

        }
        get() {
            val json = sharedPreferences.getString(USER_KEY, "")
            return if (json == "") {
                null
            } else {
                userAdapter.fromJson(json)
            }
        }
}
