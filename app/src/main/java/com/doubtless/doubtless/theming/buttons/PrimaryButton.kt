package com.doubtless.doubtless.theming.buttons

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.utils.Utils.dpToPx

class PrimaryButton constructor(
    context: Context,
    attributeSet: AttributeSet?
) : CardView(context, attributeSet) {

    private var text = "" // create a separate data class for these.

    init {
        // set attributes
        val typedArray = context.theme.obtainStyledAttributes(
            /* set = */ attributeSet,
            /* attrs = */ R.styleable.PrimaryButton,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )

        try {
            text = typedArray.getString(R.styleable.PrimaryButton_text) ?: ""
        } finally {
            typedArray.recycle()
        }

        // add textview
        val textView = TextView(context)
        this.addView(textView)

        // setup ui properties
        this.radius = 0.dpToPx() // fully rounded
        this.setCardBackgroundColor(Color.BLACK)
        this.cardElevation = 0f

        textView.setTextColor(Color.WHITE)
        textView.text = text

        textView.typeface = resources.getFont(R.font.roboto_medium)

        val padding = 8.dpToPx().toInt()
        textView.setPadding(
            /* left = */ 14.dpToPx().toInt() + padding,
            /* top = */ padding,
            /* right = */ 14.dpToPx().toInt() + padding,
            /* bottom = */ padding
        )
        textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            this.gravity = Gravity.CENTER
        }
        textView.textSize = 18f

        // other properties
        isClickable = true
    }
}