package com.doubtless.doubtless.screens.onboarding

import com.google.errorprone.annotations.Keep
import com.google.firebase.firestore.DocumentSnapshot

@Keep
data class OnBoardingAttributes(
    val tags: List<String>? = null,
    val colleges: List<String>? = null,
    val years: List<String>? = null,
    val departments: List<String>? = null,
    val purposes: List<String>? = null,
    val hobbies: List<String>? = null
) {
    companion object {
        fun parse(document: DocumentSnapshot?): OnBoardingAttributes? {
            return try {
                document!!.toObject(OnBoardingAttributes::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}