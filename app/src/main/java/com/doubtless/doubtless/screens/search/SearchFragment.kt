package com.doubtless.doubtless.screens.search

import android.app.Dialog
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
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentSearchBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.search.usecases.FetchSearchResultsUseCase
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var navigator: FragNavigator
    private lateinit var adapter: GenericFeedAdapter
    private lateinit var progressDialog: Dialog

    private lateinit var analyticsTracker: AnalyticsTracker

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)!!
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.progress_bar)
        progressDialog.setCancelable(false)

        binding.etSearch.requestFocus()
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.root, InputMethodManager.SHOW_IMPLICIT)

        if (!::adapter.isInitialized) {
            adapter =
                GenericFeedAdapter(
                    genericFeedEntities = mutableListOf(),
                    onLastItemReached = {},
                    interactionListener = object : GenericFeedAdapter.InteractionListener {
                        override fun onSearchBarClicked() {

                        }

                        override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                            analyticsTracker.trackSearchedDoubtClicked(doubtData.copy())
                            navigator.moveToDoubtDetailFragment(doubtData)
                        }

                        override fun onSignOutClicked() {

                        }

                        override fun onSubmitFeedbackClicked() {
                        }
                    })
        }

        binding.rvSearchResults.adapter = adapter

        val fetchSearchResultsUseCase =
            DoubtlessApp.getInstance().getAppCompRoot().getFetchSearchResultsUseCase()

        binding.etSearch.addTextChangedListener {

            searchJob?.cancel()

            searchJob = CoroutineScope(Dispatchers.Main).launch {

                if (it.toString().length <= 4) return@launch

                delay(1000L)
                progressDialog.show() // showProgressBar

//                val keywords =
//                    extractKeywordsUseCase.notifyNewInput(binding.etSearch.text.toString())

                val results =
                    fetchSearchResultsUseCase.getSearchResult(binding.etSearch.text.toString())

                if (!isAdded) return@launch

                progressDialog.dismiss() //hide

                if (results is FetchSearchResultsUseCase.Result.Error) {
                    Toast.makeText(requireContext(), results.message, Toast.LENGTH_SHORT).show()
                    return@launch
                }

                adapter.clearCurrentList()
                adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success)
                    .searchResult.map {
                        it.toGenericEntity()
                    })
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
                    adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success)
                        .searchResult.map {
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