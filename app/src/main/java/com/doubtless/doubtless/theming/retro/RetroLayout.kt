package com.doubtless.doubtless.theming.retro

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.doubtless.doubtless.R
import com.google.android.material.card.MaterialCardView

open class RetroLayout constructor(
    context: Context,
    attributeSet: AttributeSet?
) : CardView(context, attributeSet) {

    private val root: View
    private val contentContainer: CardView
    private val sideShadow: View
    private val bottomShadow: View

    private val isPlainStyled: Boolean

    init {
        root = LayoutInflater.from(context).inflate(R.layout.layout_retro, this, true)
        contentContainer = root.findViewById(R.id.cv_content)
        sideShadow = root.findViewById(R.id.view_rightShadow)
        bottomShadow = root.findViewById(R.id.view_bottomShadow)
        setCardBackgroundColor(Color.TRANSPARENT)
        cardElevation = 0f
        isClickable = true

        // set attributes
        val typedArray = context.theme.obtainStyledAttributes(
            /* set = */ attributeSet,
            /* attrs = */ R.styleable.RetroLayout,
            /* defStyleAttr = */ 0,
            /* defStyleRes = */ 0
        )

        try {
            isPlainStyled = typedArray.getBoolean(R.styleable.RetroLayout_isPlainStyled, false)
        } finally {
            typedArray.recycle()
        }

        // set ui properties if plain styled
        if (isPlainStyled) {
            sideShadow.setBackgroundColor(Color.BLACK)
            bottomShadow.setBackgroundColor(Color.BLACK)

            contentContainer.setCardBackgroundColor(resources.getColor(R.color.cream))
            (contentContainer as MaterialCardView).strokeWidth =
                resources.getDimension(R.dimen.retro_stroke_width).toInt()
            contentContainer.strokeColor = Color.BLACK
        }
    }

    private var lastClicked = System.currentTimeMillis()
    private val duration = 90L

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (contentContainer.childCount > 0) return // assuming the target view is already added.

        // detach user's target view from current pos in hierarchy and attach to content container.
        // child at 0th pos is "root".
        val view = getChildAt(1)
        removeView(view)

        (contentContainer).addView(view)

        val isWidthMatchParent =
            layoutParams.width == android.view.ViewGroup.LayoutParams.MATCH_PARENT

        if (isWidthMatchParent) {
            // if match parent then we need to modify some attributes.
            getChildAt(0).layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT

            (contentContainer.layoutParams as ConstraintLayout.LayoutParams).setMargins(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ resources.getDimension(R.dimen.retro_def_space).toInt(),
                /* bottom = */ 0
            )

            contentContainer.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            invalidate()
        }
    }

    private var animUpUnconsumed = false

    protected enum class STATE {
        PRESSING, PRESSED, RELEASING, RELEASED
    }

    private var currState: STATE = STATE.RELEASED

    protected var shouldPerformUpAnimationWhenReleased = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {

                if (currState != STATE.RELEASED) return false

                if (System.currentTimeMillis() - lastClicked < (duration * 2) + /* offset */ 100L) return false
                lastClicked = System.currentTimeMillis()

                animateDown()
                return true
            }

            MotionEvent.ACTION_UP -> {

                if (currState == STATE.PRESSING) {
                    performClick()

                    // inheriting class bottom nav element sets this false in order to never animate up when released.
                    if (!shouldPerformUpAnimationWhenReleased)
                        return true

                    animUpUnconsumed = true

                } else if (currState == STATE.PRESSED) {
                    performClick()

                    // inheriting class bottom nav element sets this false in order to never animate up when released.
                    if (!shouldPerformUpAnimationWhenReleased)
                        return true

                    animUpUnconsumed = true
                    animateUp()
                }

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.x !in 0f..width.toFloat() || event.y !in 0f..height.toFloat()) {

                    if (currState == STATE.PRESSING) {
                        animUpUnconsumed = true
                    } else if (currState == STATE.PRESSED) {
                        animateUp()
                    }

                    return true
                }
            }
        }

        return false
    }

    private val space = context.resources.getDimension(R.dimen.retro_def_space)
    private val contentDisplacement = 2 * space / 3
    private val shadowDisplacement = space / 3

    protected fun animateDown() {
        contentContainer.animate().translationXBy(contentDisplacement)
            .translationYBy(contentDisplacement)
            .setDuration(duration)
            .withStartAction {
                currState = STATE.PRESSING
            }
            .withEndAction {
                currState = STATE.PRESSED

                if (animUpUnconsumed)
                    animateUp()
            }.start()

        sideShadow.animate().translationXBy(-shadowDisplacement).translationYBy(-shadowDisplacement)
            .setDuration(duration)
            .start()

        bottomShadow.animate().translationYBy(-shadowDisplacement)
            .translationXBy(-shadowDisplacement)
            .setDuration(duration).start()
    }

    protected fun animateUp() {
        contentContainer.animate().translationYBy(-(contentDisplacement))
            .translationXBy(-(contentDisplacement))
            .setDuration(duration - 10)
            .withStartAction {
                currState = STATE.RELEASING
            }
            .withEndAction {
                currState = STATE.RELEASED
                animUpUnconsumed = false
            }
            .start()

        sideShadow.animate().translationXBy(shadowDisplacement)
            .translationYBy(shadowDisplacement).setDuration(duration - 10)
            .start()

        bottomShadow.animate().translationYBy(shadowDisplacement)
            .translationXBy(shadowDisplacement)
            .setDuration(duration - 10).start()
    }
}