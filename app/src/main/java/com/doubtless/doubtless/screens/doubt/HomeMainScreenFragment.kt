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
import com.doubtless.doubtless.screens.doubt.view.DoubtFilterViewModel
import com.doubtless.doubtless.screens.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator


class HomeMainScreenFragment : Fragment() {
    private var _binding: FragmentHomeMainScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: FragNavigator
    private var tags: ArrayList<String> = arrayListOf("My College", "All")
    private lateinit var viewModel: DoubtFilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)
        if (_navigator != null) navigator = _navigator

        viewModel = getViewModel()
        viewModel.fetchTags()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tags.observe(viewLifecycleOwner) {
            if (it != null) {
                setupViewPager(it)
            }
        }
        _binding!!.tvSearch.setOnClickListener {
            navigator.moveToSearchFragment()
        }

    }

    private fun setupViewPager(tags: List<String>) {
        val pagerAdapter = FilterTagsViewPagerAdapter(this@HomeMainScreenFragment, tags)
        _binding?.viewPager?.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tags[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): DoubtFilterViewModel {
        return ViewModelProvider(
            owner = this, factory = DoubtFilterViewModel.Companion.Factory(
                fetchFilterTagsUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchFilterTagsUseCase()
            )
        )[DoubtFilterViewModel::class.java]
    }
}