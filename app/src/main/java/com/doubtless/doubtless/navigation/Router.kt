package com.doubtless.doubtless.navigation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.doubtless.doubtless.OnBoardingActivity
import com.doubtless.doubtless.screens.auth.LoginActivity
import com.doubtless.doubtless.screens.main.MainActivity

class Router {

    fun moveToMainActivity(activity: Activity) {
        val i = Intent(activity, MainActivity::class.java)
        activity.startActivity(i)
    }

    fun moveToLoginActivity(activity: Activity) {
        val i = Intent(activity, LoginActivity::class.java)
        activity.startActivity(i)
    }

    fun moveToOnBoardingActivity(activity: Activity) {
        val i = Intent(activity, OnBoardingActivity::class.java) // fixme
        activity.startActivity(i)
    }

}