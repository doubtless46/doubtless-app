package com.doubtless.doubtless.utils.anims

import android.view.View
import androidx.core.view.isVisible
import com.doubtless.doubtless.utils.Utils.dpToPx

private val DURATION = 300L

fun View.animateFadeUp(duration: Long = DURATION) {
    animate().translationYBy(-16.dpToPx())
        .alpha(1f)
        .setDuration(duration)
        .withStartAction {
            isVisible = true
            alpha = 0f
            translationY += 16.dpToPx()
        }.start()
}

fun View.animateFadeDown(duration: Long = DURATION) {
    animate().translationYBy(16.dpToPx())
        .alpha(0f)
        .setDuration(duration)
        .withStartAction {
            isVisible = true
            alpha = 1f
            translationY -= 16.dpToPx()
        }.start()
}