package com.doubtless.doubtless.screens.doubt

import com.google.firebase.firestore.DocumentSnapshot

data class DoubtData(
    var id: String? = null,
    var userName: String? = null,
    var userId: String? = null,
    var userPhotoUrl: String? = null,
    var date: String? = null,
    var heading: String? = null,
    var description: String? = null,
    var upVotes: Long = 0,
    var downVotes: Long = 0,
    var score: Long = 0,
    var timeMillis: Long? = null,
    var no_answers: Int = 0
) {
    companion object {
        fun parse(documentSnapshot: DocumentSnapshot?): DoubtData? {
            return try {
                documentSnapshot!!.toObject(DoubtData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}