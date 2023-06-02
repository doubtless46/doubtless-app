package com.doubtless.doubtless.screens.answers

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class AnswerData(
    var id: String? = null,
    var doubtId: String? = null,
    var authorId: String? = null,
    var authorPhotoUrl: String? = null,
    var authorName: String? = null,
    var description: String? = null,
    var netVotes: Float = 0f,
    @ServerTimestamp
    var date: Date? = null,
) {

    companion object {
        fun parse(querySnapshot: QuerySnapshot): List<AnswerData>? {
            return try {

                val list = mutableListOf<AnswerData>()

                querySnapshot.documents.forEach {
                    try {
                        list.add(it.toObject(AnswerData::class.java)!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                list
            } catch (e: Exception) {
                null
            }
        }

        fun toAnswerDoubtEntity(answerData: AnswerData): AnswerDoubtEntity {
            return AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER, null, answerData)
        }
    }
}