package com.doubtless.doubtless.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.screens.auth.LoginActivity
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.FragmentDashboardBinding
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()

        binding.tvName.text = mAuth.currentUser?.displayName
        binding.tvUserEmail.text = mAuth.currentUser?.email

        binding.btnSignout.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                val result = withContext(Dispatchers.IO) {
                    DoubtlessApp.getInstance().getAppCompRoot().getUserManager().onUserLogoutSync()
                }

                if (!isAdded) return@launch

                if (result is UserManager.Result.LoggedOut) {

                    DoubtlessApp.getInstance().getAppCompRoot().router.moveToLoginActivity(requireActivity())
                    requireActivity().finish()

                } else if (result is UserManager.Result.Error) {

                    Toast.makeText(
                        this@DashboardFragment.requireContext(),
                        result.message,
                        Toast.LENGTH_LONG
                    ).show() // encapsulate error ui handling

                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}