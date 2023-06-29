package com.doubtless.doubtless.theming.bottomNav

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.doubtless.doubtless.screens.main.bottomNav.BottomIntractableElement

class SoberBottomNavElementLayout(context: Context, attributeSet: AttributeSet?)
    : AppCompatCheckBox(context, attributeSet), BottomIntractableElement {

    override fun onSelected() {
        isChecked = true
    }

    override fun onReselected() {
        // on reselection the checked icon will toggle, hence manually make it checked.
        isChecked = true
    }

    override fun onUnselected() {
        isChecked = false
    }

}