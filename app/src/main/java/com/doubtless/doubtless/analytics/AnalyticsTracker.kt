package com.doubtless.doubtless.analytics

import com.amplitude.android.Amplitude
import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData

class AnalyticsTracker constructor(
    private val amplitude: Amplitude,
    private val userManager: UserManager
) {

    fun trackTagsFragment(tag: String) {
        val map = hashMapOf<String, String>()
        map.putAll(getCommonAttrs())

        map["tag"] = tag

        amplitude.track("tag_feed_viewed", map)
    }


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

    fun trackFeedbackButtonClicked() {
        amplitude.track("feedback_btn_click", getCommonAttrs())

    }

    fun trackPostDoubtButtonClicked() {
        amplitude.track("post_doubt_btn_click", getCommonAttrs())
    }

    fun trackAppLaunch() {
        amplitude.track("app_launch", getCommonAttrs())
    }

    fun trackFeedRefresh() {
        // amplitude.track("feed_refresh", getCommonAttrs())
    }

    fun trackFeedNextPage(listSize: Int) {
//        amplitude.track("feed_next_page", getCommonAttrs().toMutableMap().apply {
//            this["page_size"] = listSize.toString()
//        })
    }

    fun trackDoubtUpVoted(doubtData: DoubtData) {
        val map = getCommonAttrs().toMutableMap().apply {
            this["doubt_data"] = doubtData.toString()
        }

        amplitude.track("doubt_upvote", map)
    }

    fun trackDoubtDownVoted(doubtData: DoubtData) {
        val map = getCommonAttrs().toMutableMap().apply {
            this["doubt_data"] = doubtData.toString()
        }

        amplitude.track("doubt_downvote", map)
    }

    fun trackSearchedDoubtClicked(doubtData: DoubtData) {
        val map = getCommonAttrs().toMutableMap().apply {
            this["doubt_data"] = doubtData.toString()
        }

        amplitude.track("search_doubt_click", map)
    }

    private fun getCommonAttrs(): Map<String, String> {
        val map = hashMapOf<String, String>()

        map["app_version_code"] = BuildConfig.VERSION_CODE.toString()

        val user = userManager.getCachedUserData() ?: return map

        map["user_id"] = user.id.toString()
        map["user_email"] = user.email.toString()

        if (user.local_user_attr?.tags != null)
            map["user_tags"] = user.local_user_attr!!.tags.toString()

        if (user.local_user_attr?.year != null)
            map["user_year"] = user.local_user_attr!!.year.toString()

        if (user.local_user_attr?.college != null)
            map["user_college"] = user.local_user_attr!!.college.toString()

        if (user.local_user_attr?.department != null)
            map["user_department"] = user.local_user_attr!!.department.toString()

        return map
    }

}