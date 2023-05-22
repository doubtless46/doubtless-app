package com.doubtless.doubtless.screens.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.databinding.FragmentViewDoubtsBinding
import com.doubtless.doubtless.screens.adapters.ViewDoubtsAdapter
import com.doubtless.doubtless.screens.auth.usecases.UserManager

class ViewDoubtsFragment : Fragment() {
    private var _binding: FragmentViewDoubtsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewDoubtsViewModel
    private lateinit var adapter: ViewDoubtsAdapter
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        viewModel.fetchDoubts()
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewDoubtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ViewDoubtsAdapter(mutableListOf(), onLastItemReached = {
            viewModel.fetchDoubts()
        }, user = userManager.getCachedUserData()!!)

        binding.doubtsRecyclerView.adapter = adapter
        binding.doubtsRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allDoubts.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "doubt size : ${it.size}", Toast.LENGTH_SHORT).show()
            adapter.appendDoubts(it)
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
                userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
            )
        )[ViewDoubtsViewModel::class.java]
    }

}