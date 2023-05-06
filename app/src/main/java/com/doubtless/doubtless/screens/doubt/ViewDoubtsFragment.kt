package com.doubtless.doubtless.screens.doubt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.databinding.FragmentViewDoubtsBinding
import com.doubtless.doubtless.screens.adapters.ViewDoubtsAdapter

class ViewDoubtsFragment : Fragment() {
    private var _binding: FragmentViewDoubtsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewDoubtsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ViewDoubtsViewModel::class.java]
        _binding = FragmentViewDoubtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allDoubts.observe(viewLifecycleOwner) {
            binding.doubtsRecyclerView.adapter = ViewDoubtsAdapter(it)
            binding.doubtsRecyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}