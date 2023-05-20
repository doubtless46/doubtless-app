package com.doubtless.doubtless.screens.auth.usecases

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.doubtless.doubtless.screens.auth.User
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class UserDataStorageUseCase constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    private val USER_KEY = "user_key"

    fun getUserData(): User? {
        val userString = sharedPreferences.getString(USER_KEY, null)
        return gson.fromJson(userString, User::class.java)
    }

    fun setUserData(user: User) {
        sharedPreferences.edit().apply {
            val userString = gson.toJson(user)
            this.putString(USER_KEY, userString)
            apply()
        }
    }

    fun onLogout() {
        sharedPreferences.edit().apply {
            this.putString(USER_KEY, null)
            apply()
        }
    }

}