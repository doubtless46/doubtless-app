package com.doubtless.doubtless.theming.buttons

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.main.bottomNav.BottomIntractableElement
import com.doubtless.doubtless.utils.Utils.dpToPx

class SecondaryButton constructor(
    context: Context,
    attributeSet: AttributeSet?
) : CardView(context, attributeSet), BottomIntractableElement {

    private var text = "" // create a separate data class for these.

    private var textView: TextView? = null

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
        textView = TextView(context)
        this.addView(textView)

        // setup ui properties
        this.radius = 0.dpToPx() // fully rounded
        this.cardElevation = 0f
        this.setCardBackgroundColor(context.resources.getColor(R.color.cream))
        this.foreground = context.resources.getDrawable(R.drawable.rect_border)

        textView!!.setTextColor(Color.BLACK)
        textView!!.text = text

        textView!!.typeface = resources.getFont(R.font.roboto_medium)

        val padding = 8.dpToPx().toInt()
        textView!!.setPadding(
            /* left = */ 14.dpToPx().toInt() + padding,
            /* top = */ padding,
            /* right = */ 14.dpToPx().toInt() + padding,
            /* bottom = */ padding
        )

        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        textView!!.layoutParams = lp

        textView!!.textSize = 18f

        // other properties
        isClickable = true
    }

    // bottom nav interaction

    private var isCurrentlySelected = false

    override fun onSelected() {
        isCurrentlySelected = true
        setCardBackgroundColor(resources.getColor(R.color.cream))
    }

    override fun onReselected() {

    }

    override fun onUnselected() {
        setCardBackgroundColor(resources.getColor(R.color.cream))
        isCurrentlySelected = false
    }
}