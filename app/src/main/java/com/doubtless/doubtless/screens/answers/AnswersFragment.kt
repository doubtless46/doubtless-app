package com.doubtless.doubtless.screens.answers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentAnswersBinding

class AnswersFragment : Fragment() {

    private var _binding: FragmentAnswersBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val answerViewModel = ViewModelProvider(this).get(AnswersViewModel::class.java)

        _binding = FragmentAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}