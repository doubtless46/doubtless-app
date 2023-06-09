package com.doubtless.doubtless.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.doubtless.doubtless.screens.answers.AnswersFragment
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.search.SearchFragment

class FragNavigator constructor(
    @IdRes private val containerId: Int,
    private val supportFragmentManager: FragmentManager
) {

    fun onBackPress(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0 && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.popBackStack()
            return true
        }

        return false
    }

    // TODO : make proper nav graphs
    fun moveToSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(containerId, SearchFragment())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun moveToDoubtDetailFragment(doubtData: DoubtData) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, AnswersFragment.getInstance(doubtData))
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

}