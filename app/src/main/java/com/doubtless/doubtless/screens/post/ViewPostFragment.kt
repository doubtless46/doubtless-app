package com.doubtless.doubtless.screens.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.databinding.FragmentViewPostBinding

class ViewPostFragment : Fragment() {
    private var _binding: FragmentViewPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ViewPostViewModel::class.java]
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allPosts.observe(viewLifecycleOwner) {
            binding.postsRecyclerView.adapter = ViewPostAdapter(it)
            binding.postsRecyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}