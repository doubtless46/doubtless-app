package com.doubtless.doubtless.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object Utils {

    fun Int.dpToPx(): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    )

    fun showKeyboard(context: Context?, doubtHeading: EditText) {
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(doubtHeading, InputMethodManager.SHOW_FORCED)
    }
}