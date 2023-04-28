package com.example.doubtless.theming.buttons

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.doubtless.R
import com.example.doubtless.utils.Utils.dpToPx

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
        this.radius = 100.dpToPx() // fully rounded
        this.setCardBackgroundColor(Color.BLACK)
        this.cardElevation = 0f

        textView.setTextColor(Color.WHITE)
        textView.text = text
        val padding = 8.dpToPx().toInt()
        textView.setPadding(
            /* left = */ 14.dpToPx().toInt() + padding,
            /* top = */ padding,
            /* right = */ 14.dpToPx().toInt() + padding,
            /* bottom = */ padding
        )
        textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        textView.textSize = 22f

        // other properties
        isClickable = true
    }

    override fun performClick(): Boolean {
        this.animate().scaleXBy(-0.2f).scaleYBy(-0.2f).setDuration(200L).withEndAction {
            this.animate().scaleXBy(0.2f).scaleYBy(0.2f).setDuration(200L).start()
        }.start()

        return super.performClick()
    }
}