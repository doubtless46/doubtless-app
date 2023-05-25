package com.doubtless.doubtless.utils

import android.content.res.Resources
import android.util.TypedValue
import java.util.Date
import java.util.concurrent.TimeUnit

object Utils {

    fun Int.dpToPx(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    )

    fun getTimeAgo(doubtDate: Date): String {
        val timeDiff = System.currentTimeMillis() - doubtDate.time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        val hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
        val days = TimeUnit.MILLISECONDS.toDays(timeDiff);

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hrs ago"
            minutes > 0 -> "$minutes mins ago"
            else -> "Just now"
        }
    }

}