package com.doubtless.doubtless.screens.home.entities

import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.search.SearchResult

data class FeedEntity(
    val type: Int,
    val doubt: DoubtData? = null,
    val search_doubt: SearchResult? = null
) {
    companion object {
        const val TYPE_DOUBT = 1
        const val TYPE_SEARCH = 2
        const val TYPE_SEARCH_RESULT = 3
        const val TYPE_USER_PROFILE = 4
        const val TYPE_CREATE_POLL = 5

        fun getSearchEntity(): FeedEntity {
            return FeedEntity(TYPE_SEARCH, null, null)
        }
    }
}