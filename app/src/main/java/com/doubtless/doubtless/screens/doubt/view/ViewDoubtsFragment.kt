package com.doubtless.doubtless.screens.doubt.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentViewDoubtsBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.auth.exception.UserNotFoundException
import com.doubtless.doubtless.screens.auth.login.LoginUtilsImpl
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.home.entities.FeedConfig
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.utils.Resource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson

class ViewDoubtsFragment : Fragment() {

    private var _binding: FragmentViewDoubtsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewDoubtsViewModel
    private lateinit var adapter: GenericFeedAdapter
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker
    private lateinit var navigator: FragNavigator
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var feedConfig: FeedConfig
    private lateinit var tag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide)
        // exitTransition = inflater.inflateTransition(R.transition.fade)

        tag = arguments?.getString("tag")!!

        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
        remoteConfig = DoubtlessApp.getInstance().getAppCompRoot().getRemoteConfig()
        feedConfig = FeedConfig.parse(Gson(), remoteConfig)
            ?: FeedConfig(
                pageSize = 10,
                recentPostsCount = 6,
                feedDebounce = 3000,
                searchDebounce = 600
            )

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)

        if (_navigator != null)
            navigator = _navigator

        viewModel = getViewModel()
        viewModel.fetchDoubts(forPageOne = true, feedTag = tag!!)
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

        binding.llProgressBar.visibility = View.VISIBLE //show progress bar

        binding.layoutSwipe.setOnRefreshListener {

            if (System.currentTimeMillis() - lastRefreshed < feedConfig.feedDebounce) {
                Log.d("feed debounced", feedConfig.feedDebounce.toString())
                binding.layoutSwipe.isRefreshing = false
                return@setOnRefreshListener
            }

            lastRefreshed = System.currentTimeMillis()
            binding.layoutSwipe.isRefreshing = true
            viewModel.refreshList(tag = tag)
            adapter.clearCurrentList()
        }

        adapter = GenericFeedAdapter(
            genericFeedEntities = viewModel.homeEntities.toMutableList(),
            onLastItemReached = {
                viewModel.fetchDoubts(feedTag = tag)
            },
            interactionListener = object : GenericFeedAdapter.InteractionListener {
                override fun onSearchBarClicked() {
                    navigator.moveToSearchFragment()
                }

                override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                    // note that this we are not sending a copy of doubtData here,
                    // hence if netVotes are changed on the other screen then it will change here too.
                    // this solves our problem but can cause complications on long term.
                    navigator.moveToDoubtDetailFragment(doubtData)
                }

                override fun onSignOutClicked() {

                }

                override fun onSubmitFeedbackClicked() {
                }

                override fun onDeleteAccountClicked() {
                }
            })

        // how is rv restoring its scroll pos when switching tabs?
        binding.doubtsRecyclerView.adapter = adapter
        binding.doubtsRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.fetchedHomeEntities.observe(viewLifecycleOwner) { result ->
            binding.llProgressBar.visibility = View.GONE //hide progress bar
            binding.layoutSwipe.isRefreshing = false
            result?.let {
                when (result) {
                    is Resource.Success<*> -> {
                        result.data?.let {
                            adapter.appendDoubts(it as List<FeedEntity>)
                        }
                    }
                    is Resource.Error<*> -> {
                        when (result.error) {
                            is UserNotFoundException -> {
                                if (!isAdded) return@observe
                                FirebaseCrashlytics.getInstance()
                                    .recordException(Exception("current user is null"))
                                LoginUtilsImpl.logOutUser(analyticsTracker, requireActivity())
                                result.message?.let { it1 -> showToast(it1) }
                            }
                            else -> {
                                result.message?.let {
                                    showToast(it)
                                }
                            }
                        }
                    }
                    is Resource.Loading<*> -> {
                    }
                }
            }

        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
            .show()
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
                    .getFetchHomeFeedUseCase(feedConfig),
                analyticsTracker = analyticsTracker,
                userManager = userManager
            )
        )[ViewDoubtsViewModel::class.java]
    }

}