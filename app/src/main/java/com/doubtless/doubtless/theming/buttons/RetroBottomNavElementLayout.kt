package com.doubtless.doubtless.theming.buttons

import android.content.Context
import android.util.AttributeSet
import com.doubtless.doubtless.screens.main.bottomNav.BottomIntractableElement
import com.doubtless.doubtless.theming.retro.RetroLayout

class RetroBottomNavElementLayout(context: Context, attributeSet: AttributeSet?) :
    RetroLayout(context, attributeSet), BottomIntractableElement {

    init {
        // if a bottom nav element is selected we don't want to release it until another element is selected.
        shouldPerformUpAnimationWhenReleased = false
    }

    // bottom nav interaction

    private var isCurrentlySelected: Boolean? = null

    override fun onSelected() {

        // Elements get anim down only when they are clicked,
        // Hence initially we need to call animateDown manually.
        if (isCurrentlySelected == null) {
            animateDown()
        }

        isCurrentlySelected = true
    }

    override fun onUnselected() {

        if (isCurrentlySelected == true) {
            animateUp() // initially we don't want to animate up the already released element
        }

        isCurrentlySelected = false
    }

    override fun onReselected() {

    }

}