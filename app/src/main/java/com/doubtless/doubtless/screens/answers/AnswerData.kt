package com.doubtless.doubtless.screens.answers

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class AnswerData(
    var id :String?,
    var doubtId :String?=null,
    var authorId :String?=null,
    var authorPhotoUrl :String?=null,
    var authorName :String?=null,
    var description :String?=null,

    var netVotes: Float = 0f,

    @ServerTimestamp
    var date: Date? = null,
)