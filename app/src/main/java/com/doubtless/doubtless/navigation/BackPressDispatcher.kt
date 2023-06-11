package com.doubtless.doubtless.navigation

interface BackPressDispatcher {
    fun registerBackPress(listener: OnBackPressListener)
    fun unregisterBackPress(listener: OnBackPressListener)
}