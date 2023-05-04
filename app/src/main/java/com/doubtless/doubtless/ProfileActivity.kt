package com.doubtless.doubtless

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile2.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        mAuth = FirebaseAuth.getInstance()

        tv_name.text = mAuth.currentUser?.displayName
        tv_user_email.text = mAuth.currentUser?.email


        btn_signout.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googlesigninclient = GoogleSignIn.getClient(this,gso)
            googlesigninclient.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}