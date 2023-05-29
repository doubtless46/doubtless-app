package com.doubtless.doubtless.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentMainBinding
import com.doubtless.doubtless.screens.dashboard.DashboardFragment
import com.doubtless.doubtless.screens.doubt.CreateDoubtFragment
import com.doubtless.doubtless.screens.doubt.ViewDoubtsFragment
import com.doubtless.doubtless.screens.main.bottomNav.OnSelectedItemChangedListener

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val bottomNavFragments =
        listOf(ViewDoubtsFragment(), CreateDoubtFragment(), DashboardFragment())

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

                // add fragments to fm if not already given this callback
                // gets triggered for initial default element selection.
                if (!areBottomNavFragmentsAdded) {
                    val transaction = childFragmentManager.beginTransaction()

                    bottomNavFragments.forEach {
                        transaction.add(R.id.bottom_nav_fragment_container, it)
                    }

                    transaction.commit()

                    areBottomNavFragmentsAdded = true
                }

                // TODO : support for animation
                // detach unselected fragments and attach the selected one

                val transaction = childFragmentManager.beginTransaction()

                bottomNavFragments.forEachIndexed { index, fragment ->
                    if (index != newIndex)
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