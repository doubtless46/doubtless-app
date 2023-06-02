package com.doubtless.doubtless.screens.home.entities

import com.doubtless.doubtless.screens.doubt.DoubtData

data class FeedEntity(
    val type: Int,
    val doubt: DoubtData? = null
) {
    companion object {
        const val TYPE_DOUBT = 1
        const val TYPE_SEARCH = 2

        fun getSearchEntity(): FeedEntity {
            return FeedEntity(TYPE_SEARCH, null)
        }
    }
}