package com.doubtless.doubtless.constants

import com.doubtless.doubtless.BuildConfig

class FirestoreCollection {

    companion object {
        const val USER = "users"
        const val USER_ATTR = "user_attr"
        const val MiscAppData = "misc_app_data"

        val AllDoubts = if (false) {
            "AllDoubts_Test"
        } else {
            "AllDoubts"
        }

        val DoubtAnswer = "Answer"

        val DOUBT_VOTING_DATA = if (false) {
            "doubt_voting_data_test"
        } else {
            "doubt_voting_data"
        }

        val ANSWER_VOTING_DATA = if (false) {
            "answer_voting_data_test"
        } else {
            "answer_voting_data"
        }

        val UPVOTE_DATA_USERS = "upvoted_users"
        val DOWNVOTE_DATA_USERS = "downvoted_users"


    }

}