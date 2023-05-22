package com.doubtless.doubtless.screens.auth

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val attributes: HashMap<String, String>? = null
)
