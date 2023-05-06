package com.doubtless.doubtless.screens.doubt

data class DoubtData(
    var id: String,
    var userName: String,
    var date: String,
    var heading: String,
    var description: String,
    var answers: Any?,
    var upVotes: Long,
    var downVotes: Long
)
