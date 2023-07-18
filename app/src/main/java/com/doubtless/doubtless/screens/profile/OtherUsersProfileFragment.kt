package com.doubtless.doubtless.screens.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentOtherUsersProfileBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment

class OtherUsersProfileFragment : Fragment() {

    private var _binding: FragmentOtherUsersProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: OtherUsersProfileViewModel
    private lateinit var navigator: FragNavigator
    private lateinit var adapter: GenericFeedAdapter

    private lateinit var userId: String
    private lateinit var tracker: AnalyticsTracker
    private var onCreateEventUnConsumed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

        var _navigator: FragNavigator? = null

        // uuhh!!
        val currentSelectedFrag =
            (requireActivity() as MainActivity).getMainFragment()?.getCurrentSelectedElement()

        // encapsulate this logic
        if (currentSelectedFrag is MainFragment.CurrentSelectedBottomNavFrag.HomeFrag) {
            _navigator = DoubtlessApp.getInstance().getAppCompRoot()
                .getHomeFragNavigator(requireActivity() as MainActivity)
        }

        if (currentSelectedFrag is MainFragment.CurrentSelectedBottomNavFrag.DashboardFrag) {
            _navigator = DoubtlessApp.getInstance().getAppCompRoot()
                .getDashboardFragNavigator(requireActivity() as MainActivity)
        }

        if (_navigator != null) navigator = _navigator

        val _userId = arguments?.getString("user_id")


        if (_userId == null) {
            navigator.onBackPress()
            return
        }

        userId = _userId
        viewModel = getViewModel()
        viewModel.fetchUserDetails(userId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtherUsersProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (onCreateEventUnConsumed) {
            viewModel.fetchDoubts(userId, true)
            onCreateEventUnConsumed = false
        }

        val feedList = mutableListOf<FeedEntity>()
        feedList.add(FeedEntity(FeedEntity.TYPE_USER_PROFILE, null))

        var userData: com.doubtless.doubtless.screens.auth.User? = viewModel.fetchedUserData.value
        var doubts: List<FeedEntity>? = null

        viewModel.fetchedUserData.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            viewModel.fetchDoubts(userId)


            val user = it
            userData = user

            if (!::adapter.isInitialized) {
                adapter = GenericFeedAdapter(
                    genericFeedEntities = feedList,
                    user = user,
                    onLastItemReached = {
                        viewModel.fetchDoubts(userId)
                    },
                    interactionListener = object : GenericFeedAdapter.InteractionListener {
                        override fun onUserImageClicked(userId: String) {}

                        override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                            navigator.moveToDoubtDetailFragment(doubtData)
                        }

                        override fun onSignOutClicked() {}

                        override fun onSubmitFeedbackClicked() {}

                        override fun onDeleteAccountClicked() {}
                    }
                )
            }
            binding.profileRecyclerView.adapter = adapter
            binding.profileRecyclerView.layoutManager = LinearLayoutManager(context)

        }

        viewModel.fetchedHomeEntities.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            adapter.appendDoubts(it)
            doubts = it
            Log.i("ObserveFeed", it.toTypedArray().contentToString())
            viewModel.notifyFetchedDoubtsConsumed()
        }

        binding.icBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


    }

    private fun getViewModel(): OtherUsersProfileViewModel {
        return ViewModelProvider(
            owner = this, factory = OtherUsersProfileViewModel.Companion.Factory(
                fetchUserProfileFeedUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchUserProfileFeedUseCase(),
                analyticsTracker = tracker
            )
        )[OtherUsersProfileViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun getInstance(userId: String): OtherUsersProfileFragment {
            return OtherUsersProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("user_id", userId)
                }
            }
        }

    }

}