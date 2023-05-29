package com.doubtless.doubtless.screens.doubt.usecases

import android.content.SharedPreferences

class DoubtDataSharedPrefUseCase constructor(
    private val sharedPreferences: SharedPreferences
) {


    fun getSavedDoubtData(): Pair<String?, String?> {
        val headingText = sharedPreferences.getString("headingText", null)
        val descriptionText = sharedPreferences.getString("descriptionText", null)
        return Pair(headingText, descriptionText)
    }

    fun saveDoubtData(headingText: String, descriptionText: String) {
        val editor = sharedPreferences.edit()
        editor.putString("headingText", headingText)
        editor.putString("descriptionText", descriptionText)
        editor.apply()
    }
}