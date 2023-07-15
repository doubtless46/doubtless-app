package com.doubtless.doubtless.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.AnswersFragment
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.profile.OtherUsersProfileFragment
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
            .setReorderingAllowed(true)
            .commitAllowingStateLoss()
    }

    fun moveToDoubtDetailFragment(doubtData: DoubtData) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                /* enter = */ R.anim.slide_in_right,
                /* exit = */ R.anim.slide_out_left,
                /* popEnter = */ R.anim.slide_in_left,
                /* popExit = */ R.anim.slide_out_right
            )
            .replace(containerId, AnswersFragment.getInstance(doubtData))
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .commitAllowingStateLoss()
    }

    fun moveToOtherUsersProfileFragment(userId: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                /* enter = */ R.anim.slide_in_right,
                /* exit = */ R.anim.slide_out_left,
                /* popEnter = */ R.anim.slide_in_left,
                /* popExit = */ R.anim.slide_out_right
            )
            .replace(containerId, OtherUsersProfileFragment.getInstance(userId))
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .commitAllowingStateLoss()
    }

}