package com.doubtless.doubtless.screens.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.databinding.FragmentHomeMainScreenBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.doubt.view.HomeMainScreenViewModel
import com.doubtless.doubtless.screens.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale


class HomeMainScreenFragment : Fragment() {
    private var _binding: FragmentHomeMainScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: FragNavigator
    private lateinit var viewModel: HomeMainScreenViewModel
    private lateinit var user: com.doubtless.doubtless.screens.auth.User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = DoubtlessApp.getInstance().getAppCompRoot().getUserManager().getCachedUserData()!!

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
        capitalizedTagList[0] = "${user.local_user_attr!!.college!!} only"
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = capitalizedTagList[position]
        }.attach()

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