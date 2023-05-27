package com.doubtless.doubtless.screens.answer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentAnswerBinding


class AnswerFragment : Fragment() {

    private var _binding: FragmentAnswerBinding? = null

    private val binding get() = _binding!!
    private lateinit var tracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAnswerBinding.inflate(inflater, container, false)

        return binding.root
    }

}