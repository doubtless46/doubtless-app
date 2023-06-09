package com.doubtless.doubtless.screens.auth

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
@Keep
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
    @get:Exclude val document_id: String? = null,
    @get:Exclude val local_user_attr: UserAttributes? = null
)

@Keep
data class UserAttributes(
    val tags: List<String>? = null,
    val hobbies: List<String>? = null,
    val year: String? = null,
    val department: String? = null,
    val college: String? = null,
    val purpose: String? = null
)
