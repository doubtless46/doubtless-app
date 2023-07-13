package com.doubtless.doubtless.screens.doubt

import android.os.Parcelable
import androidx.annotation.Keep
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Keep
data class DoubtData(
    @get:PropertyName("doubt_id")
    @set:PropertyName("doubt_id")
    @SerializedName("doubt_id")
    var id: String? = null,
    @SerializedName("author_name")
    @get:PropertyName("author_name")
    @set:PropertyName("author_name")
    var userName: String? = null,
    @SerializedName("author_id")
    @get:PropertyName("author_id")
    @set:PropertyName("author_id")
    var userId: String? = null,
    @SerializedName("author_photo_url")
    @get:PropertyName("author_photo_url")
    @set:PropertyName("author_photo_url")
    var userPhotoUrl: String? = null,
    var heading: String? = null,
    var description: String? = null,
    @SerializedName("author_college")
    @get:PropertyName("author_college")
    @set:PropertyName("author_college")
    var college: String? = null,
    @SerializedName("author_year")
    @get:PropertyName("author_year")
    @set:PropertyName("author_year")
    var year: String? = null,

    @SerializedName("net_votes")
    @get:PropertyName("net_votes")
    @set:PropertyName("net_votes")

    // this is var as netVotes are changed on upvote and downvote.
    var netVotes: Float = 0f,


    @SerializedName("count_answers")
    @get:PropertyName("count_answers")
    @set:PropertyName("count_answers")
    var no_answers: Int = 0,
    @ServerTimestamp
    @SerializedName("created_on")
    @get:PropertyName("created_on")
    @set:PropertyName("created_on")
    var date: Date? = null,
    @SerializedName("tags")
    @get:PropertyName("tags")
    @set:PropertyName("tags")
    var tags: List<String>? = null,
    @SerializedName("is_trending")
    @get:PropertyName("is_trending")
    @set:PropertyName("is_trending")
    var isTrending: Boolean = false,
    @SerializedName("xp_count")
    @get:PropertyName("xp_count")
    @set:PropertyName("xp_count")
    var xpCount: Long = 0,
    var mentorsDpWhoInteracted: List<String> = mutableListOf(),
    @SerializedName("iv_content")
    @get:PropertyName("image_content_url")
    @set:PropertyName("image_content_url")
    var imageContentUrl: String? = null
) : Parcelable {
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

data class PublishDoubtRequest(
    @SerializedName("author_name")
    var userName: String? = null,
    @SerializedName("author_id")
    var userId: String? = null,
    @SerializedName("author_photo_url")
    var userPhotoUrl: String? = null,
    @SerializedName("heading")
    var heading: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("author_college")
    var college: String? = null,
    @SerializedName("author_year")
    var year: String? = null,
    @SerializedName("net_votes")
    var netVotes: Float = 0f,
    @SerializedName("tags")
    var tags: List<String>? = null,
    @SerializedName("keywords")
    var keywords: List<String>? = null,
    @SerializedName("xp_count")
    var xpCount: Long = 0
)