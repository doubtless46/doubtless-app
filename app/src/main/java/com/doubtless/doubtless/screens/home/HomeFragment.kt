package com.doubtless.doubtless.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentHomeBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.OnBackPressListener
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsFragment
import com.doubtless.doubtless.screens.main.MainActivity

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navigator: FragNavigator

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.home_container, ViewDoubtsFragment())
            }
        }

        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)!!

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private val onBackPressListener = object : OnBackPressListener {
        override fun onBackPress(): Boolean {
            return navigator.onBackPress()
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).registerBackPress(onBackPressListener)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).unregisterBackPress(onBackPressListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}