package com.doubtless.doubtless.screens.answers

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import java.util.Date

data class AnswerData(
    @get:PropertyName("answer_id")
    @set:PropertyName("answer_id")
    @SerializedName("answer_id")
    var id: String? = null,
    @SerializedName("doubt_id")
    @get:PropertyName("doubt_id")
    @set:PropertyName("doubt_id")
    var doubtId: String? = null,
    @SerializedName("author_id")
    @get:PropertyName("author_id")
    @set:PropertyName("author_id")
    var authorId: String? = null,
    @SerializedName("author_photo_url")
    @get:PropertyName("author_photo_url")
    @set:PropertyName("author_photo_url")
    var authorPhotoUrl: String? = null,
    @SerializedName("author_name")
    @get:PropertyName("author_name")
    @set:PropertyName("author_name")
    var authorName: String? = null,
    @SerializedName("author_college")
    @get:PropertyName("author_college")
    @set:PropertyName("author_college")
    var authorCollege: String? = null,
    @SerializedName("author_year")
    @get:PropertyName("author_year")
    @set:PropertyName("author_year")
    var authorYear: String? = null,
    var description: String? = null,
    @SerializedName("net_votes")
    @get:PropertyName("net_votes")
    @set:PropertyName("net_votes")
    var netVotes: Float = 0f,
    @ServerTimestamp
    @SerializedName("created_on")
    @get:PropertyName("created_on")
    @set:PropertyName("created_on")
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

data class PublishAnswerRequest(
    @SerializedName("doubt_id")
    var doubtId: String? = null,
    @SerializedName("author_id")
    var authorId: String? = null,
    @SerializedName("author_photo_url")
    var authorPhotoUrl: String? = null,
    @SerializedName("author_name")
    var authorName: String? = null,
    @SerializedName("author_college")
    var authorCollege: String? = null,
    @SerializedName("author_year")
    var authorYear: String? = null,
    @SerializedName("description")
    var description: String? = null
) {
    companion object {
        fun toAnswerData(
            publishAnswerRequest: PublishAnswerRequest
        ): AnswerData {
            return AnswerData(
                id = null,
                doubtId = publishAnswerRequest.doubtId,
                authorId = publishAnswerRequest.authorId,
                authorPhotoUrl = publishAnswerRequest.authorPhotoUrl,
                authorName = publishAnswerRequest.authorName,
                authorCollege = publishAnswerRequest.authorCollege,
                authorYear = publishAnswerRequest.authorYear,
                description = publishAnswerRequest.description,
                netVotes = 0f,
                date = Date()
            )
        }
    }
}

data class PublishAnswerResponse(
    @get:PropertyName("answer_id")
    @set:PropertyName("answer_id")
    @SerializedName("answer_id")
    var id: String? = null,
    @SerializedName("doubt_id")
    @get:PropertyName("doubt_id")
    @set:PropertyName("doubt_id")
    var doubtId: String? = null,
    @SerializedName("author_id")
    @get:PropertyName("author_id")
    @set:PropertyName("author_id")
    var authorId: String? = null,
    @SerializedName("author_photo_url")
    @get:PropertyName("author_photo_url")
    @set:PropertyName("author_photo_url")
    var authorPhotoUrl: String? = null,
    @SerializedName("author_name")
    @get:PropertyName("author_name")
    @set:PropertyName("author_name")
    var authorName: String? = null,
    @SerializedName("author_college")
    @get:PropertyName("author_college")
    @set:PropertyName("author_college")
    var authorCollege: String? = null,
    @SerializedName("author_year")
    @get:PropertyName("author_year")
    @set:PropertyName("author_year")
    var authorYear: String? = null,
    var description: String? = null,
    @SerializedName("net_votes")
    @get:PropertyName("net_votes")
    @set:PropertyName("net_votes")
    var netVotes: Float = 0f
) {
    fun toAnswerData(): AnswerData {
        return AnswerData(
            id = this.id,
            doubtId = this.doubtId,
            authorId = this.authorId,
            authorPhotoUrl = this.authorPhotoUrl,
            authorName = this.authorName,
            authorCollege = this.authorCollege,
            authorYear = this.authorYear,
            description = this.description,
            netVotes = this.netVotes,
            date = null
        )
    }
}