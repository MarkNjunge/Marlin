package com.marknjunge.marlin.data.local

import android.content.Context
import com.marknjunge.marlin.data.model.AccessToken
import com.orhanobut.hawk.Hawk

interface PreferencesStorage {
    var accessToken: AccessToken?
}

class PreferencesStorageImpl(context: Context) : PreferencesStorage {
    init {
        Hawk.init(context).build()
    }

    private val ACCESS_TOKEN_KEY = "access_token_key"

    override var accessToken: AccessToken?
        set(value) {
            if (value == null) {
                Hawk.delete(ACCESS_TOKEN_KEY)
            } else {
                Hawk.put(ACCESS_TOKEN_KEY, value)
            }
        }
        get() = Hawk.get(ACCESS_TOKEN_KEY)
}
