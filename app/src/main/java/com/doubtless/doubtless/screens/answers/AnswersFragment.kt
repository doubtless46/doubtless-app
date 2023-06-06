package com.doubtless.doubtless.screens.answers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentAnswersBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.answers.usecases.PublishAnswerUseCase
import com.doubtless.doubtless.screens.answers.viewholder.EnterAnswerViewHolder
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsViewModel
import com.doubtless.doubtless.screens.main.MainActivity

class AnswersFragment : Fragment() {

    private var _binding: FragmentAnswersBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: AnswersViewModel
    private lateinit var adapter: AnswerDoubtsAdapter
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker
    private lateinit var navigator: FragNavigator

    private lateinit var doubtData: DoubtData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide)
        //exitTransition = inflater.inflateTransition(R.transition.fade)


        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

        val _navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getHomeFragNavigator(requireActivity() as MainActivity)

        if (_navigator != null)
            navigator = _navigator

        val _doubtData = arguments?.getParcelable("doubt_data") as DoubtData?

        if (_doubtData == null) {
            navigator.onBackPress()
            return
        }

        doubtData = _doubtData
        viewModel = getViewModel(doubtData)
        viewModel.fetchAnswers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::adapter.isInitialized) {
            adapter = AnswerDoubtsAdapter(
                user = userManager.getCachedUserData()!!,
                doubtAnswerEntities = mutableListOf(),
                onLastItemReached = {
                    /* no-op */
                },
                interactionListener = object : AnswerDoubtsAdapter.InteractionListener {
                    override fun onLayoutClicked() {

                    }

                    override fun onDoubtClicked(doubtData: DoubtData, position: Int) {

                    }

                    override fun onAnswerClicked(answerData: AnswerData, position: Int) {

                    }

                    override fun onAnswerPublish(publishAnswerDTO: EnterAnswerViewHolder.PublishAnswerDTO) {
                        viewModel.publishAnswer(
                            PublishAnswerRequest(
                                doubtId = doubtData.id,
                                authorId = userManager.getCachedUserData()!!.id,
                                authorName = userManager.getCachedUserData()!!.name,
                                authorPhotoUrl = userManager.getCachedUserData()!!.photoUrl,
                                authorCollege = userManager.getCachedUserData()!!.local_user_attr!!.college,
                                authorYear = userManager.getCachedUserData()!!.local_user_attr!!.year,
                                description = publishAnswerDTO.description
                            )
                        )
                    }

                }
            )
        }

        binding.answerRecyclerView.adapter = adapter
        binding.answerRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.answerDoubtEntities.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            adapter.appendAnswer(it)
            viewModel.notifyAnswersConsumed()
        }

        viewModel.publishAnswerStatus.observe(viewLifecycleOwner) {

            if (it is PublishAnswerUseCase.Result.Success) {
                Toast.makeText(requireContext(), "Successfully posted!", Toast.LENGTH_SHORT).show()
                adapter.appendAnswerAtFirst(answerData = it.answerData)
                // this will increase the count across screens as the same reference was passed to the arguments.
                // Its generally not a good thing to do.t(it.answerData)
                doubtData.no_answers += 1
            }

            if (it is PublishAnswerUseCase.Result.Error)
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getViewModel(doubtData: DoubtData): AnswersViewModel {
        return ViewModelProvider(
            owner = this,
            factory = AnswersViewModel.Companion.Factory(
                userManager = userManager,
                fetchAnswerUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchAnswerUseCase(doubtData.id!!),
                doubtData = doubtData,
                publishAnswerUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getPublishAnswerUseCase()
            )
        )[AnswersViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun getInstance(doubtData: DoubtData): AnswersFragment {
            return AnswersFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("doubt_data", doubtData)
                }
            }
        }
    }

}