package com.doubtless.doubtless.navigation

import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.doubtless.doubtless.screens.search.SearchFragment

class FragNavigator constructor(
    @IdRes private val containerId: Int,
    private val supportFragmentManager: FragmentManager
) {

    // TODO : make proper nav graphs
    fun moveToSearchFragment() {

        Log.d("frag manager",  supportFragmentManager.backStackEntryCount.toString())

        supportFragmentManager.beginTransaction()
            .replace(containerId, SearchFragment())
            .addToBackStack(null)
            .commit()

    }

}