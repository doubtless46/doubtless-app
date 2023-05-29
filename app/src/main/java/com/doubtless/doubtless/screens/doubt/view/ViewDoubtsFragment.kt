package com.doubtless.doubtless.screens.doubt.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentViewDoubtsBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.main.MainActivity

class ViewDoubtsFragment : Fragment() {

    private var _binding: FragmentViewDoubtsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewDoubtsViewModel
    private lateinit var adapter: GenericFeedAdapter
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker
    private lateinit var navigator: FragNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot().getHomeFragNavigator(requireActivity() as MainActivity)

        if (_navigator != null)
            navigator = _navigator

        viewModel = getViewModel()
        viewModel.fetchDoubts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewDoubtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // for debouncing
        var lastRefreshed = System.currentTimeMillis()

        binding.layoutSwipe.setOnRefreshListener {

            if (System.currentTimeMillis() - lastRefreshed < 3 * 1000L) {
                binding.layoutSwipe.isRefreshing = false
                return@setOnRefreshListener
            }

            lastRefreshed = System.currentTimeMillis()

            binding.layoutSwipe.isRefreshing = true
            viewModel.refreshList()
            adapter.clearCurrentList()
        }

        adapter = GenericFeedAdapter(
            homeEntities = viewModel.homeEntities.toMutableList(),
            onLastItemReached = {
                viewModel.fetchDoubts()
            },
            interactionListener = object : GenericFeedAdapter.InteractionListener {
                override fun onSearchBarClicked() {
                    navigator.moveToSearchFragment()
                }

                override fun onDoubtClicked(doubtData: DoubtData, position: Int) {

                }
            })

        // how is rv restoring its scroll pos when switching tabs?
        binding.doubtsRecyclerView.adapter = adapter
        binding.doubtsRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.fetchedHomeEntities.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            adapter.appendDoubts(it)
            viewModel.notifyFetchedDoubtsConsumed()
            binding.layoutSwipe.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getViewModel(): ViewDoubtsViewModel {
        return ViewModelProvider(
            owner = this,
            factory = ViewDoubtsViewModel.Companion.Factory(
                fetchHomeFeedUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchHomeFeedUseCase(),
                analyticsTracker = analyticsTracker,
                userManager = userManager
            )
        )[ViewDoubtsViewModel::class.java]
    }

}