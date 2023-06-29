package com.doubtless.doubtless.screens.auth.login

import android.app.Activity
import android.widget.Toast
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object LoginUtilsImpl : LoginUtils {

    override fun logOutUser(analyticsTracker: AnalyticsTracker, activity: Activity) {
        analyticsTracker.trackLogout()
        CoroutineScope(Dispatchers.Main).launch {

            val result = withContext(Dispatchers.IO) {
                DoubtlessApp.getInstance().getAppCompRoot().getUserManager().onUserLogoutSync()
            }

            if (result is UserManager.Result.LoggedOut) {

                DoubtlessApp.getInstance().getAppCompRoot().router.moveToLoginActivity(
                    activity
                )
                activity.finish()

            } else if (result is UserManager.Result.Error) {

                Toast.makeText(
                    activity, result.message, Toast.LENGTH_LONG
                ).show() // encapsulate error ui handling
            }
        }
    }
}