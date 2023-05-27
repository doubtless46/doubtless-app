package com.doubtless.doubtless.screens.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.databinding.FragmentSearchBinding
import com.doubtless.doubtless.databinding.LayoutHomeSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)

        requireActivity().onBackPressedDispatcher.addCallback {
            if (parentFragmentManager.backStackEntryCount > 0 && !isStateSaved) {
                parentFragmentManager.popBackStackImmediate()
            }

            this.isEnabled = false
        }

        binding.etSearch.requestFocus()
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.etSearch, InputMethodManager.SHOW_FORCED)

        var textSearch = binding.etSearch.text

        val extractKeywordsUseCase = DoubtlessApp.getInstance().getAppCompRoot().getExtractKeywordsUseCase()

        binding.etSearch.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {

                val words = extractKeywordsUseCase.notifyNewInput(it.toString()).filterNot {
                    it in RedundantWords.nonNounWords
                }

                Log.d("search", words.toString())
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

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