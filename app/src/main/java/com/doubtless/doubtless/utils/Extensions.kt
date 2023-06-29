package com.doubtless.doubtless.utils

import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimatorRes

fun View.addStateListAnimation(@AnimatorRes animation: Int) {
    this.stateListAnimator = AnimatorInflater.loadStateListAnimator(
        this.context,
        animation
    )
}

fun View.hideSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}