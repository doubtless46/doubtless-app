package com.doubtless.doubtless.screens.inAppNotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentInappNotifBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.navigation.OnBackPressListener
import com.doubtless.doubtless.screens.doubt.usecases.FetchDoubtDataFromDoubtIdUseCase
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.screens.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InAppNotificationFragment : Fragment() {

    private var _binding: FragmentInappNotifBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: InAppNotificationAdapter
    private lateinit var navigator: FragNavigator
    private lateinit var fetchDoubtDataFromDoubtIdUseCase: FetchDoubtDataFromDoubtIdUseCase

    private val viewModel: InAppNotificationViewModel by viewModels(
        factoryProducer = {
            InAppNotificationViewModel.Companion.Factory(
                DoubtlessApp.getInstance().getAppCompRoot().getFetchNotificationUseCase(),
                DoubtlessApp.getInstance().getAppCompRoot().getMarkInAppNotificationsReadUseCase()
            )
        }
    )

    private var onCreateEventUnConsumed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEventUnConsumed = true
        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getInAppFragNavigator(requireActivity() as MainActivity)!!
        fetchDoubtDataFromDoubtIdUseCase =
            DoubtlessApp.getInstance().getAppCompRoot().getFetchDoubtDataFromDoubtIdUseCase()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInappNotifBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (onCreateEventUnConsumed) {
            viewModel.fetchNotification()
            onCreateEventUnConsumed = false
        }

        if (::adapter.isInitialized) {
            binding.rvNotif.adapter = adapter
        }

        viewModel.notificationStatus.observe(viewLifecycleOwner) {
            when (it) {
                is InAppNotificationViewModel.Result.Success -> {

                    binding.progressBar.isVisible = false

                    if (::adapter.isInitialized == false) {

                        adapter = InAppNotificationAdapter(
                            it.notifications.toMutableList(),
                            object : InAppNotificationAdapter.InteractionListener {
                                override fun onPostAnswerNotifClicked(doubtId: String) {

                                    CoroutineScope(Dispatchers.IO).launch {

                                        val doubtData =
                                            fetchDoubtDataFromDoubtIdUseCase.getDoubtData(doubtId)
                                                ?: return@launch

                                        if (!isAdded) return@launch

                                        navigator.moveToDoubtDetailFragment(doubtData)
                                    }
                                }
                            })

                        binding.rvNotif.adapter = adapter

                        if (it.additionalError != null)
                            Toast.makeText(requireContext(), it.additionalError, Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        adapter.setNewNotifications(it.notifications.toMutableList())
                    }
                }

                is InAppNotificationViewModel.Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is InAppNotificationViewModel.Result.NoData -> {

                    if (it.additionalError != null)
                        Toast.makeText(requireContext(), it.additionalError, Toast.LENGTH_SHORT)
                            .show()

                    binding.progressBar.isVisible = false
                }

                is InAppNotificationViewModel.Result.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        }
    }

    private val onBackPressListener = object : OnBackPressListener {
        override fun onBackPress(): Boolean {
            (parentFragment as? MainFragment)?.selectHomeBottomNavElement()
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).registerBackPress(onBackPressListener)
    }

    override fun onPause() {
        super.onPause()
        viewModel.markNotificationsAsRead()
        (requireActivity() as MainActivity).unregisterBackPress(onBackPressListener)
    }

}