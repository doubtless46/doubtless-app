package com.doubtless.doubtless.screens.auth

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    @get:Exclude val document_id: String? = null,
    @get:Exclude val local_user_attr: UserAttributes? = null
)

data class UserAttributes(
    val tags: List<String>? = null,
    val hobbies: List<String>? = null,
    val year: String? = null,
    val department: String? = null,
    val college: String? = null,
    val purpose: String? = null
)
