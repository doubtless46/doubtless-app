package com.doubtless.doubtless.screens.search

import android.os.Parcelable
import androidx.annotation.Keep
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Keep
data class SearchResult(
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
    var netVotes: Float = 0f,
    @SerializedName("count_answers")
    @get:PropertyName("count_answers")
    @set:PropertyName("count_answers")
    var no_answers: Int = 0,
    @SerializedName("tags")
    @get:PropertyName("tags")
    @set:PropertyName("tags")
    var tags: List<String>? = null
): Parcelable {
    fun toGenericEntity(): FeedEntity {
        return FeedEntity(type = FeedEntity.TYPE_SEARCH_RESULT, null, this)
    }

    fun toDoubtData() : DoubtData {
        return DoubtData(
            id = this.id,
            userName = this.userName,
            userId = this.userId,
            userPhotoUrl = this.userPhotoUrl,
            heading = this.heading,
            description = this.description,
            college = this.college,
            year = this.year,
            netVotes = this.netVotes,
            no_answers = this.no_answers,
            date = null,
            tags = this.tags
        )
    }
}