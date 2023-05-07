package com.doubtless.doubtless.analytics

import com.amplitude.android.Amplitude

class AnalyticsTracker(private val amplitude: Amplitude) {

    fun trackMainActivity() {
        amplitude.track("main_activity")
    }

}