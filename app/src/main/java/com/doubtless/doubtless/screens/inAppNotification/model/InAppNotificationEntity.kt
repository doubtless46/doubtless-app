package com.doubtless.doubtless.screens.inAppNotification.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.getField

@Keep
@Entity(tableName = "InAppNotifications")
@IgnoreExtraProperties
data class InAppNotificationEntity(
    @get:Exclude
    @PrimaryKey
    val notificationId: String,
    val answerAuthorId: String? = null,
    val answerAuthorName: String? = null,
    val answerId: String? = null,
    val authorPhotoUrl: String? = null,
    @ServerTimestamp
    val createdOn: Timestamp? = null,
    val description: String? = null,
    val doubtAuthorId: String? = null,
    val doubtHeading: String? = null,
    val doubtId: String? = null,
    val type: String? = TYPE_POST_ANSWER,
    val isRead: Boolean = false
) {
    companion object {

        const val TYPE_POST_ANSWER = "postAnswer"

        fun fromDocumentSnapshot(documentSnapshot: DocumentSnapshot?): InAppNotificationEntity? {
            return try {
                val notif = InAppNotificationEntity(
                    notificationId = documentSnapshot!!.id,
                    answerAuthorId = documentSnapshot.getField("answer_author_id"),
                    answerAuthorName = documentSnapshot.getField("answer_author_name"),
                    answerId = documentSnapshot.getField("answer_id"),
                    authorPhotoUrl = documentSnapshot.getField("author_photo_url"),
                    createdOn = documentSnapshot.getField("created_on"),
                    description = documentSnapshot.getField("answer_description"),
                    doubtAuthorId = documentSnapshot.getField("doubt_author_id"),
                    doubtHeading = documentSnapshot.getField("doubt_heading"),
                    doubtId = documentSnapshot.getField("doubt_id"),
                    type = documentSnapshot.getField("type")
                )

                if (notif.type?.equals(TYPE_POST_ANSWER) == false) {
                    return null
                }

                return notif

            } catch (e: Exception) {
                null
            }
        }
    }
}
