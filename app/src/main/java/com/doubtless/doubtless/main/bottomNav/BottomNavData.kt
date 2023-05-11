package com.doubtless.doubtless.main.bottomNav

object BottomNavMenu {
    const val defaultSelectedIndex = 0
}

/**
 * gets triggered also when the initial default index is selected without user interaction
 */
interface OnSelectedItemChangedListener {
    fun onNewSelectedIndex(newIndex: Int)
}

interface BottomIntractableElement {
    fun onSelected()
    fun onUnselected()
}