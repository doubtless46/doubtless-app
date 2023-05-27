package com.doubtless.doubtless.constants

import com.doubtless.doubtless.BuildConfig

class FirestoreCollection {

    companion object {
        const val USER = "users"
        val AllDoubts = if (BuildConfig.DEBUG) {
            "AllDoubts_Test"
        } else {
            "AllDoubts"
        }
    }

}