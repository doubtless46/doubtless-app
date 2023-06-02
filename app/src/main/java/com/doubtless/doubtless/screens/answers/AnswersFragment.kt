package com.doubtless.doubtless.screens.answers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentAnswersBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.main.MainActivity

class AnswersFragment : Fragment() {

    private var _binding: FragmentAnswersBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: AnswersViewModel
    private lateinit var adapter: AnswerDoubtsAdapter
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker
    private lateinit var navigator: FragNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)

        if (_navigator != null)
            navigator = _navigator

        viewModel = getViewModel()
        //viewModel.fetchAnswers()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // for debouncing
        var lastRefreshed = System.currentTimeMillis()

        binding.layoutSwipeAnswer.setOnRefreshListener {

            if (System.currentTimeMillis() - lastRefreshed < 3 * 1000L) {
                binding.layoutSwipeAnswer.isRefreshing = false
                return@setOnRefreshListener
            }

            lastRefreshed = System.currentTimeMillis()

            binding.layoutSwipeAnswer.isRefreshing = true
            //viewModel.refreshList()
            adapter.clearCurrentList()
        }

        adapter = AnswerDoubtsAdapter(
            doubtAnswerEntities = viewModel.answerDoubtEntities.toMutableList(),
            onLastItemReached = {
                viewModel.fetchAnswers()
            },
            interactionListener = object : AnswerDoubtsAdapter.InteractionListener {
                override fun onLayoutClicked() {
                    TODO("Not yet implemented")
                }

                override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                    TODO("Not yet implemented")
                }

                override fun onAnswerClicked(answerData: AnswerData, position: Int) {
                    TODO("Not yet implemented")
                }

            }
        )

        binding.answerRecyclerView.adapter = adapter
        binding.answerRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun getViewModel(): AnswersViewModel {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}