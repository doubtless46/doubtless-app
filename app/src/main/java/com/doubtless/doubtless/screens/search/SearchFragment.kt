package com.doubtless.doubtless.screens.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentSearchBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.search.usecases.FetchSearchResultsUseCase
import com.doubtless.doubtless.utils.hideSoftKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var navigator: FragNavigator
    private lateinit var adapter: GenericFeedAdapter

    private lateinit var analyticsTracker: AnalyticsTracker

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)!!
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        binding.etSearch.post {
            val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            binding.etSearch.requestFocus()
        }

        if (!::adapter.isInitialized) {
            adapter = GenericFeedAdapter(genericFeedEntities = mutableListOf(),
                onLastItemReached = {},
                interactionListener = object : GenericFeedAdapter.InteractionListener {
                    override fun onUserImageClicked(userId: String) {
                        navigator.moveToOtherUsersProfileFragment(userId)
                    }

                    override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                        analyticsTracker.trackSearchedDoubtClicked(doubtData.copy())
                        navigator.moveToDoubtDetailFragment(doubtData)
                    }

                    override fun onSignOutClicked() {}

                    override fun onSubmitFeedbackClicked() {}

                    override fun onDeleteAccountClicked() {}
                })
        }

        binding.rvSearchResults.adapter = adapter

        val fetchSearchResultsUseCase =
            DoubtlessApp.getInstance().getAppCompRoot().getFetchSearchResultsUseCase()

        binding.etSearch.addTextChangedListener {

            searchJob?.cancel()

            searchJob = CoroutineScope(Dispatchers.Main).launch {


                binding.progressSearch.visibility = View.VISIBLE
                if (it.toString().length <= 1) {
                    delay(1000L)
                    binding.progressSearch.visibility = View.GONE
                    return@launch
                }

                adapter.clearCurrentList() // remove list when change in text

                delay(1000L)

//                val keywords =
//                    extractKeywordsUseCase.notifyNewInput(binding.etSearch.text.toString())

                val results =
                    fetchSearchResultsUseCase.getSearchResult(binding.etSearch.text.toString())

                if (!isAdded) return@launch

                if (results is FetchSearchResultsUseCase.Result.Error) {
                    Toast.makeText(requireContext(), results.message, Toast.LENGTH_SHORT).show()
                    return@launch
                }
                adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success).searchResult.map {
                    it.toGenericEntity()
                })
                binding.progressSearch.visibility = View.GONE
                requireView().hideSoftKeyboard()
                binding.etSearch.clearFocus()
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CoroutineScope(Dispatchers.Main).launch {
                    val results =
                        fetchSearchResultsUseCase.getSearchResult(binding.etSearch.text.toString())

                    if (results is FetchSearchResultsUseCase.Result.Error) {
                        Toast.makeText(requireContext(), results.message, Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    adapter.clearCurrentList()
                    adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success).searchResult.map {
                        it.toGenericEntity()
                    })
                }
            }

            true
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }

}