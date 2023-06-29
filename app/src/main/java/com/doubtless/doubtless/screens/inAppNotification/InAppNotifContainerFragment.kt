package com.doubtless.doubtless.screens.inAppNotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.OnBackPressListener
import com.doubtless.doubtless.screens.dashboard.DashboardFragment
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment

class InAppNotificationContainerFragment: Fragment(R.layout.fragment_home) {

    private lateinit var navigator: FragNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.bottomNav_child_container, InAppNotificationFragment())
            }
        }

        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getInAppFragNavigator(requireActivity() as MainActivity)!!
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