package com.doubtless.doubtless.screens.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentHomeMainScreenBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.view.HomeMainScreenViewModel
import com.doubtless.doubtless.screens.main.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale


class HomeMainScreenFragment : Fragment() {
    private var _binding: FragmentHomeMainScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: FragNavigator
    private lateinit var viewModel: HomeMainScreenViewModel
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeMainScreenBinding.inflate(inflater, container, false)
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.purple)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)
        if (_navigator != null) navigator = _navigator

        viewModel = getViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.tags.value.isNullOrEmpty()) {
            viewModel.fetchTags()

        }

        viewModel.tags.observe(viewLifecycleOwner) {
            if (binding.viewPager.adapter == null) {
                setupViewPager(it)
            }
        }
        binding.tvSearch.setOnClickListener {
            navigator.moveToSearchFragment()
        }

    }

    private fun setupViewPager(tags: List<String>) {
        val pagerAdapter = FilterTagsViewPagerAdapter(this@HomeMainScreenFragment, tags)
        binding.viewPager.adapter = pagerAdapter
        val capitalizedTagList = tags.map { s ->
            s.replaceFirstChar {
                if (it.isLowerCase()) it.uppercase(
                    Locale.ROOT
                ) else it.toString()
            }
        }.toMutableList()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position == 1) {
                tab.text = "${userManager.getCachedUserData()?.local_user_attr!!.college!!} only"
                return@TabLayoutMediator
            }
            tab.text = capitalizedTagList[position]
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                analyticsTracker.trackTagsFragment(capitalizedTagList[tab!!.position])
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): HomeMainScreenViewModel {
        return ViewModelProvider(
            owner = this, factory = HomeMainScreenViewModel.Companion.Factory(
                fetchFilterTagsUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchFilterTagsUseCase()
            )
        )[HomeMainScreenViewModel::class.java]
    }

}