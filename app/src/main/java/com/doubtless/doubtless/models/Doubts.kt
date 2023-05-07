package com.doubtless.doubtless.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doubts(
    val user_id: String = "",
    val name: String = "",
    val reputation: String = "",
    val doubt: String = "",
    val answer: String = "",
    val total_answers: String = "",
    var id: String = ""
):Parcelable

