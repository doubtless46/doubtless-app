package com.doubtless.doubtless.constants

import com.doubtless.doubtless.BuildConfig

class FirestoreCollection {

    companion object {
        const val USER = "users"
        const val USER_ATTR = "user_attr"
        const val MiscAppData = "misc_app_data"
        val AllDoubts = if (BuildConfig.DEBUG) {
            "AllDoubts_Test"
        } else {
            "AllDoubts"
        }

        val DoubtAnswer = "Doubt_Answer"
    }

}