package com.doubtless.doubtless.screens.doubt

import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class DoubtData(
    var id: String? = null,
    var userName: String? = null,
    var userId: String? = null,
    var userPhotoUrl: String? = null,
    var heading: String? = null,
    var description: String? = null,
    var netVotes: Float = 0f,
    var score: Long = 0,
    var no_answers: Int = 0,
    @ServerTimestamp
    var date: Date? = null,
    var tags: List<String>? = null
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

    fun toHomeEntity(): FeedEntity {
        return FeedEntity(type = FeedEntity.TYPE_DOUBT, this)
    }
}