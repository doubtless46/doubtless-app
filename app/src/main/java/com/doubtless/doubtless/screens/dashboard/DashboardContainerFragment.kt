package com.doubtless.doubtless.screens.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.OnBackPressListener
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment

class DashboardContainerFragment : Fragment(R.layout.fragment_home) {

    private lateinit var navigator: FragNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.bottomNav_child_container, DashboardFragment())
            }
        }

        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getDashboardFragNavigator(requireActivity() as MainActivity)!!
    }

    private val onBackPressListener = object : OnBackPressListener {
        override fun onBackPress(): Boolean {

            val backPressConsumed = navigator.onBackPress()

            return if (backPressConsumed)
                true
            else {
                (parentFragment as? MainFragment)?.selectHomeBottomNavElement()
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).registerBackPress(onBackPressListener)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).unregisterBackPress(onBackPressListener)
    }

}