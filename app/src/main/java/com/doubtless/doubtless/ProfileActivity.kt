package com.doubtless.doubtless

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.doubtless.doubtless.databinding.ActivityLoginBinding
import com.doubtless.doubtless.databinding.ActivityProfile2Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var _binding : ActivityProfile2Binding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        _binding = ActivityProfile2Binding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()

        binding.tvName.text = mAuth.currentUser?.displayName
        binding.tvUserEmail.text = mAuth.currentUser?.email


        binding.btnSignout.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this,gso)
            googleSignInClient.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}