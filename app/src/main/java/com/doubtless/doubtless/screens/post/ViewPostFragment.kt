package com.doubtless.doubtless.screens.post

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doubtless.doubtless.R

class ViewPostFragment : Fragment() {

    companion object {
        fun newInstance() = ViewPostFragment()
    }

    private lateinit var viewModel: ViewPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_post, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}