package com.doubtless.doubtless.screens.auth

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Keep
@Parcelize
data class User(
    @SerializedName("id")
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String? = null,
    @SerializedName("name")
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @SerializedName("email")
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    @SerializedName("phoneNumber")
    @get:PropertyName("phoneNumber")
    @set:PropertyName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("photoUrl")
    @get:PropertyName("photoUrl")
    @set:PropertyName("photoUrl")
    var photoUrl: String? = null,
    @SerializedName("xpCount")
    @get:PropertyName("xpCount")
    @set:PropertyName("xpCount")
    var xpCount: Long? = 0,
    @get:Exclude val document_id: String? = null,
    @get:Exclude var local_user_attr: UserAttributes? = null
) : Parcelable {
    companion object {
        fun parse(documentSnapshot: DocumentSnapshot?): User? {
            return try {
                documentSnapshot!!.toObject(User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}

@Keep
@Parcelize
data class UserAttributes(
    val tags: List<String>? = null,
    val hobbies: List<String>? = null,
    val year: String? = null,
    val department: String? = null,
    val college: String? = null,
    val purpose: String? = null
) : Parcelable {
    companion object {
        fun parse(documentSnapshot: DocumentSnapshot?): UserAttributes? {
            return try {
                documentSnapshot!!.toObject(UserAttributes::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
