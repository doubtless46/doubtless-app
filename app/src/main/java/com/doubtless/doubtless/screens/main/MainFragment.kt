package com.doubtless.doubtless.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentMainBinding
import com.doubtless.doubtless.screens.main.bottomNav.OnSelectedItemChangedListener
import com.doubtless.doubtless.screens.dashboard.DashboardFragment
import com.doubtless.doubtless.screens.doubt.create.CreateDoubtFragment
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsFragment
import com.doubtless.doubtless.screens.home.HomeFragment

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val bottomNavFragments =
        listOf(HomeFragment(), CreateDoubtFragment(), DashboardFragment())

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

                val transaction = childFragmentManager.beginTransaction()

                // add fragments to fm if not already given this callback
                // gets triggered for initial default element selection.
                if (!areBottomNavFragmentsAdded) {

                    bottomNavFragments.forEach {
                        transaction.add(
                            R.id.bottom_nav_fragment_container, it,
                            "mainfrag_$newIndex"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}