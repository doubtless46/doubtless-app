package com.doubtless.doubtless.screens.onboarding

import com.google.errorprone.annotations.Keep
import com.google.firebase.firestore.DocumentSnapshot

@Suppress("UNCHECKED_CAST")
@Keep
data class OnBoardingAttributes(
    val tags: List<String>? = listOf(),
    val colleges: List<String>? = listOf(),
    val years: List<String>? = listOf(),
    val departments: List<String>? = listOf(),
    val purposes: List<String>? = listOf(),
    val hobbies: List<String>? = listOf()
) {
    companion object {
        fun parse(document: DocumentSnapshot?): OnBoardingAttributes? {
            return try {
                // toObject didn't work after trying many solutions, apparently Keep annotation didn't work too.
                // hence parse manually :/
                OnBoardingAttributes(
                    tags = document!!.get("tags") as List<String>?,
                    colleges = document.get("colleges") as List<String>?,
                    years = document.get("years") as List<String>?,
                    departments = document.get("departments") as List<String>?,
                    purposes = document.get("purposes") as List<String>?,
                    hobbies = document.get("hobbies") as List<String>?,
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}