package com.doubtless.doubtless.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

object Utils {

    fun Int.dpToPx(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}