package com.doubtless.doubtless.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentMainBinding
import com.doubtless.doubtless.screens.dashboard.DashboardContainerFragment
import com.doubtless.doubtless.screens.main.bottomNav.OnSelectedItemChangedListener
import com.doubtless.doubtless.screens.dashboard.DashboardFragment
import com.doubtless.doubtless.screens.doubt.create.CreateDoubtFragment
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsFragment
import com.doubtless.doubtless.screens.home.HomeFragment
import com.doubtless.doubtless.screens.inAppNotification.InAppNotificationContainerFragment
import com.doubtless.doubtless.screens.inAppNotification.InAppNotificationFragment

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val bottomNavFragments =
        listOf(
            HomeFragment(),
            CreateDoubtFragment(),
            InAppNotificationContainerFragment(),
            DashboardContainerFragment()
        )

    private var areBottomNavFragmentsAdded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.retroBottomNav.setOnSelectedItemChangedListener(object :
            OnSelectedItemChangedListener {
            override fun onNewSelectedIndex(newIndex: Int) {
                val transaction = childFragmentManager.beginTransaction()

                // add fragments to fm if not already given this callback
                // gets triggered for initial default element selection.
                if (!areBottomNavFragmentsAdded) {

                    bottomNavFragments.forEachIndexed { index, frag ->
                        transaction.add(
                            R.id.bottom_nav_fragment_container, frag,
                            "mainfrag_$index"
                        )
                    }

                    bottomNavFragments.forEachIndexed { index, fragment ->
                        if (index != newIndex)
                            transaction.detach(fragment)
                    }

                    transaction.attach(bottomNavFragments[newIndex])
                        .commit() // happens on click hence not need to allow state loss.

                    areBottomNavFragmentsAdded = true

                    return
                }

                // TODO : support for animation
                // detach unselected fragments and attach the selected one

                bottomNavFragments.forEachIndexed { index, fragment ->
                    if (index != newIndex && !fragment.isDetached)
                        transaction.detach(fragment)
                }

                transaction.attach(bottomNavFragments[newIndex])
                    .commit() // happens on click hence not need to allow state loss.
            }
        })

        return binding.root
    }

    fun selectHomeBottomNavElement() {
        if (!binding.btmNavHome.isChecked) {
            binding.btmNavHome.callOnClick()
        }
    }

    sealed class CurrentSelectedBottomNavFrag {
        object HomeFrag : CurrentSelectedBottomNavFrag()
        object CreateFrag : CurrentSelectedBottomNavFrag()
        object InAppNotificationFrag : CurrentSelectedBottomNavFrag()
        object DashboardFrag : CurrentSelectedBottomNavFrag()
        object UnknownFrag : CurrentSelectedBottomNavFrag()
    }

    fun getCurrentSelectedElement(): CurrentSelectedBottomNavFrag {

        val index = binding.retroBottomNav.getCurrentSelectedIndex()

        return when (index) {
            0 -> CurrentSelectedBottomNavFrag.HomeFrag
            1 -> CurrentSelectedBottomNavFrag.CreateFrag
            2 -> CurrentSelectedBottomNavFrag.InAppNotificationFrag
            3 -> CurrentSelectedBottomNavFrag.DashboardFrag
            else -> {
                CurrentSelectedBottomNavFrag.UnknownFrag
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}