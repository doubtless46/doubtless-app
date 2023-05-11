package com.doubtless.doubtless

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.doubtless.doubtless.databinding.ActivityLoginBinding
import com.doubtless.doubtless.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignin.setOnClickListener {

            binding.progress.visibility = View.VISIBLE
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1001)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && data != null) {

            var account: GoogleSignInAccount? = null

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                account = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
                return
            }


            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        binding.progress.visibility = View.GONE

                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                        binding.progress.visibility = View.GONE
                    }
                }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}