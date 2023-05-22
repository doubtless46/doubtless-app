package com.doubtless.doubtless.screens.doubt

data class DoubtData(
    var id: String? = null,
    var userName: String? = null,
    var userPhotoUrl : String? = null,
    var date: String? = null,
    var heading: String? = null,
    var description: String? = null,
    var upVotes: Long = 0,
    var downVotes: Long = 0,
    var score: Long = 0,
    var timeMillis: Long? = null
)