package com.doubtless.doubtless.analytics

import com.amplitude.android.Amplitude
import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.usecases.UserManager

class AnalyticsTracker constructor(
    private val amplitude: Amplitude,
    private val userManager: UserManager
) {

    fun trackLoginStarted() {
        val map = hashMapOf<String, String>()
        map.putAll(getCommonAttrs())

        map["did_finish"] = "false"

        amplitude.track("login", map)
    }

    fun trackLoginSuccess(isNewUser: Boolean) {
        val map = hashMapOf<String, String>()
        map.putAll(getCommonAttrs())

        map["did_finish"] = "true"
        map["new_user"] = isNewUser.toString()

        amplitude.track("login", map)
    }

    fun trackLogout() {
        amplitude.track("logout", getCommonAttrs())
    }

    fun trackPostDoubtButtonClicked() {
        amplitude.track("post_doubt_btn_click", getCommonAttrs())
    }

    fun trackAppLaunch() {
        amplitude.track("app_launch", getCommonAttrs())
    }

    fun trackFeedRefresh() {
        amplitude.track("feed_refresh", getCommonAttrs())
    }

    fun trackFeedNextPage(listSize: Int) {
        amplitude.track("feed_next_page", getCommonAttrs().toMutableMap().apply {
            this["page_size"] = listSize.toString()
        })
    }

    private fun getCommonAttrs(): Map<String, String> {
        val map = hashMapOf<String, String>()

        map["app_version_code"] = BuildConfig.VERSION_CODE.toString()

        val user = userManager.getCachedUserData() ?: return map
        map["user_id"] = user.id.toString()

        return map
    }

}