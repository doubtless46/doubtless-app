package com.doubtless.doubtless.navigation

import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.AnswersFragment
import com.doubtless.doubtless.screens.dashboard.DashboardFragment
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
            .commit()
    }

    fun moveToDoubtDetailFragment(doubtData: DoubtData) {
        supportFragmentManager.beginTransaction()
            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
//            .setCustomAnimations(
//                /* enter = */ R.anim.enter_right,
//                /* exit = */ R.anim.exit_left
//            )
        .replace(containerId, AnswersFragment.getInstance(doubtData))
            .addToBackStack(null)
            .commit()
    }

}