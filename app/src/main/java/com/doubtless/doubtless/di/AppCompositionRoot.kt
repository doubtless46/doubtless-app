package com.doubtless.doubtless.di

import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker

class AppCompositionRoot (appContext: DoubtlessApp) {

    private lateinit var analyticsTracker: AnalyticsTracker

    private val amplitude = Amplitude(
        Configuration(
            apiKey = "9ccdf7b8da7390a82fd779a2de0c6b1b",
            context = appContext
        )
    )

    @Synchronized
    fun getAnalyticsTracker(): AnalyticsTracker {

        if (::analyticsTracker.isInitialized == false) {
            analyticsTracker = AnalyticsTracker(amplitude)
        }

        return analyticsTracker
    }

}