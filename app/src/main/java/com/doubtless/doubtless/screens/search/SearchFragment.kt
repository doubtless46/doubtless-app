package com.doubtless.doubtless.screens.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.databinding.FragmentSearchBinding
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.search.usecases.FetchSearchResultsUseCase
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var searchJob : Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        binding.etSearch.requestFocus()
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.root, InputMethodManager.SHOW_IMPLICIT)

        val adapter =
            GenericFeedAdapter(mutableListOf(), {}, object : GenericFeedAdapter.InteractionListener {
                override fun onSearchBarClicked() {

                }

                override fun onDoubtClicked(doubtData: DoubtData, position: Int) {

                }
            })

        binding.rvSearchResults.adapter = adapter

        val extractKeywordsUseCase =
            DoubtlessApp.getInstance().getAppCompRoot().getExtractKeywordsUseCase()
        val fetchSearchResultsUseCase =
            DoubtlessApp.getInstance().getAppCompRoot().getFetchSearchResultsUseCase()

        binding.etSearch.addTextChangedListener {

            searchJob?.cancel()

            searchJob = CoroutineScope(Dispatchers.Main).launch {

                delay(500L)

//                val keywords =
//                    extractKeywordsUseCase.notifyNewInput(binding.etSearch.text.toString())

                val results = fetchSearchResultsUseCase.getSearchResult(binding.etSearch.text.toString())

                if (results is FetchSearchResultsUseCase.Result.Error) return@launch

                adapter.clearCurrentList()
                adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success)
                    .searchResult.map {
                        it.toHomeEntity()
                    })
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CoroutineScope(Dispatchers.Main).launch {
//                    val keywords =
//                        extractKeywordsUseCase.notifyNewInput()
                    val results = fetchSearchResultsUseCase.getSearchResult(binding.etSearch.text.toString())

                    if (results is FetchSearchResultsUseCase.Result.Error) return@launch

                    adapter.clearCurrentList()
                    adapter.appendDoubts((results as FetchSearchResultsUseCase.Result.Success)
                        .searchResult.map {
                            it.toHomeEntity()
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
    }

}