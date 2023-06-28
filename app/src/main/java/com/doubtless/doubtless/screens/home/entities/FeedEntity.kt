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
        const val TYPE_BUTTONS = 5
        const val TYPE_POLL_VOTE = 6

        fun getSearchEntity(): FeedEntity {
            return FeedEntity(TYPE_SEARCH, null, null)
        }

        fun getOptionButtons(): FeedEntity{
            return FeedEntity(TYPE_BUTTONS, null, null)
        }
        fun getPollView(): FeedEntity{
            return FeedEntity(TYPE_POLL_VOTE, null, null)
        }
    }
}