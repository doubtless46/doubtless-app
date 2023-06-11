package com.doubtless.doubtless.screens.home.entities

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class FeedConfig(
    @SerializedName("page_size")
    val pageSize: Int,
    @SerializedName("recent_posts_count")
    val recentPostsCount: Int,
    @SerializedName("feed_debounce")
    val feedDebounce: Int,
    @SerializedName("search_debounce")
    val searchDebounce: Int,
) {
    companion object {
        fun parse(gson: Gson, remoteConfig: FirebaseRemoteConfig): FeedConfig? {
            return try {
                gson.fromJson(remoteConfig["feed_config"].asString(), FeedConfig::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}