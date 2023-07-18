package com.doubtless.doubtless.screens.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.BottomSheetDeleteAccountBinding
import com.doubtless.doubtless.databinding.FragmentDashboardBinding
import com.doubtless.doubtless.navigation.FragNavigator
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.common.GenericFeedAdapter
import com.doubtless.doubtless.screens.dashboard.usecases.DeleteAccountUseCase
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.doubtless.doubtless.screens.main.MainActivity
import com.doubtless.doubtless.theming.retro.RetroLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var userManager: UserManager
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: GenericFeedAdapter
    private lateinit var navigator: FragNavigator
    private lateinit var bottomSheetBinding: BottomSheetDeleteAccountBinding


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tracker: AnalyticsTracker

    private var onCreateEventUnConsumed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
        mAuth = FirebaseAuth.getInstance()
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        navigator = DoubtlessApp.getInstance().getAppCompRoot()
            .getDashboardFragNavigator(requireActivity() as MainActivity)!!
        viewModel = getViewModel()

        onCreateEventUnConsumed = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (onCreateEventUnConsumed) {
            viewModel.fetchDoubts(forPageOne = true)
            onCreateEventUnConsumed = false
        }

        val feedList = mutableListOf<FeedEntity>()
        feedList.add(FeedEntity(FeedEntity.TYPE_USER_PROFILE, null))

        if (!::adapter.isInitialized) {
            adapter = GenericFeedAdapter(genericFeedEntities = feedList, onLastItemReached = {
                viewModel.fetchDoubts()
            }, interactionListener = object : GenericFeedAdapter.InteractionListener {
                override fun onUserImageClicked(userId: String) {
                }


                override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                    navigator.moveToDoubtDetailFragment(doubtData)
                }

                override fun onSignOutClicked() {
                    signOut()
                }

                override fun onSubmitFeedbackClicked() {
                    tracker.trackFeedbackButtonClicked()
                    submitFeedback()
                }

                override fun onDeleteAccountClicked() {
                    showBottomSheet()
                }
            })
        }

        binding.profileRecyclerView.adapter = adapter
        binding.profileRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.fetchedHomeEntities.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            adapter.appendDoubts(it)
            Log.i("ObserveFeed", it.toTypedArray().contentToString())
            viewModel.notifyFetchedDoubtsConsumed()
        }

        viewModel.accountDeletionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DeleteAccountUseCase.Result.Error -> {
                    bottomSheetBinding.progress.visibility = View.GONE
                    Toast.makeText(
                        requireContext(), result.message, Toast.LENGTH_SHORT
                    ).show()
                }

                is DeleteAccountUseCase.Result.Success -> {

                    signOut()
                    Toast.makeText(
                        requireContext(), "Account Successfully Deleted", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun showBottomSheet() {
        // Replace R.layout.bottom_sheet_delete_account with your actual layout name
        bottomSheetBinding = BottomSheetDeleteAccountBinding.inflate(layoutInflater)
        val bottomSheetView = bottomSheetBinding.root
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)


        val confirmDeleteButton = bottomSheetView.findViewById<RetroLayout>(R.id.btnConfirmDelete)
        val cancelButton = bottomSheetView.findViewById<Button>(R.id.btnCancel)
        val deleteProgress = bottomSheetDialog.findViewById<ProgressBar>(R.id.progress)
        confirmDeleteButton.setOnClickListener {
            deleteProgress!!.visibility = View.VISIBLE
            viewModel.deleteAccount()
        }
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun getViewModel(): DashboardViewModel {
        return ViewModelProvider(
            owner = this, factory = DashboardViewModel.Companion.Factory(
                deleteAccountUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getDeleteAccountUseCase(),
                fetchUserProfileFeedUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                    .getFetchUserProfileFeedUseCase(),
                analyticsTracker = tracker,
                userManager = userManager
            )
        )[DashboardViewModel::class.java]
    }

    private fun submitFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("doubtless46@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback by ${userManager.getCachedUserData()!!.name}")
            putExtra(Intent.EXTRA_TEXT, "Enter Feedback Here")
        }
        startActivity(intent)

    }

    private fun signOut() {
        tracker.trackLogout()

        CoroutineScope(Dispatchers.Main).launch {

            val result = withContext(Dispatchers.IO) {
                DoubtlessApp.getInstance().getAppCompRoot().getUserManager().onUserLogoutSync()
            }

            if (!isAdded) return@launch

            if (result is UserManager.Result.LoggedOut) {

                DoubtlessApp.getInstance().getAppCompRoot().router.moveToLoginActivity(
                    requireActivity()
                )
                requireActivity().finish()

            } else if (result is UserManager.Result.Error) {

                Toast.makeText(
                    this@DashboardFragment.requireContext(), result.message, Toast.LENGTH_LONG
                ).show() // encapsulate error ui handling

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}