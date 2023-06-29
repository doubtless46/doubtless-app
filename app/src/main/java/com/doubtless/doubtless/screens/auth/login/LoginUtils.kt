package com.doubtless.doubtless.screens.auth.login

import android.app.Activity
import com.doubtless.doubtless.analytics.AnalyticsTracker

interface LoginUtils {
    fun logOutUser(analyticsTracker: AnalyticsTracker, activity: Activity)
}