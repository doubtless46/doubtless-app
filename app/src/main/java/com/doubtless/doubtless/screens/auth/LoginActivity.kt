package com.doubtless.doubtless.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.ActivityLoginBinding
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var analyticsTracker: AnalyticsTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
        mAuth = FirebaseAuth.getInstance()

        setupUi()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignin.setOnClickListener {

            analyticsTracker.trackLoginStarted()

            binding.progress.visibility = View.VISIBLE
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1001)
        }
    }

    private fun setupUi() {
        window.statusBarColor = getColor(R.color.purple)

        Glide.with(this)
            .load("https://lh3.googleusercontent.com/a/AGNmyxaceCDqTACCSoa1e3VimHXoAEQ4IBSYbPk8YTU-J5U=s96-c")
            .circleCrop()
            .into(binding.layoutDoubt.findViewById<ImageView>(R.id.iv_dp))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && data != null) {

            var account: GoogleSignInAccount? = null

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                account = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                Toast.makeText(this, "Error : ${exception.message}", Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
                return
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.user != null) {
                        onAuthSuccess(task)
                    } else {
                        onError(task.exception?.message ?: "some error occurred")
                    }
                }
        }

    }

    private fun onAuthSuccess(task: Task<AuthResult>) {
        // notify login to user manager
        val serverUser = task.result.user!!

        val userManager =
            DoubtlessApp.getInstance().getAppCompRoot().getUserManager()

        CoroutineScope(Dispatchers.Main).launch {

            val result = withContext(Dispatchers.IO) {
                userManager.registerAndGetIfNewUserSync(serverUser)
            }

            if (this@LoginActivity.isDestroyed) return@launch

            if (result is UserManager.Result.Error) {
                Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
                return@launch
            }

            val isNewUser = (result as UserManager.Result.Success).isNewUser

            analyticsTracker.trackLoginSuccess(isNewUser)

            val isOldUserWithNoOnboardingData = result.isOldUserWithNoOnboarding

            if (isOldUserWithNoOnboardingData || isNewUser) {
                DoubtlessApp.getInstance()
                    .getAppCompRoot().router.moveToOnBoardingActivity(this@LoginActivity)
            } else {
                DoubtlessApp.getInstance()
                    .getAppCompRoot().router.moveToMainActivity(this@LoginActivity)
            }

            window.statusBarColor = getColor(R.color.white)
            finish()
        }
    }

    private fun onError(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT)
            .show()
        binding.progress.visibility = View.GONE
    }
}