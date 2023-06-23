package com.doubtless.doubtless.utils

import android.animation.AnimatorInflater
import android.view.View
import androidx.annotation.AnimatorRes

fun View.addStateListAnimation(@AnimatorRes animation: Int) {
    this.stateListAnimator = AnimatorInflater.loadStateListAnimator(
        this.context,
        animation
    )
}