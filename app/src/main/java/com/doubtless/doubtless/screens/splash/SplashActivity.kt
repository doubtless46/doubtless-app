package com.doubtless.doubtless.screens.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.di.AppCompositionRoot
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.utils.anims.animateFadeUp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.Main.immediate).launch {
            val view = findViewById<TextView>(R.id.tv_title)
            delay(200L)
            view.animateFadeUp(800L)

            userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
            checkMinAppVersionRequired()
        }
    }

    private fun moveToNextActivity() {
        if (userManager.getLoggedInUser() != null
            // if not onboarding, move to login screen ask to onboard.
            && userManager.getLoggedInUser()?.local_user_attr != null) {

            DoubtlessApp.getInstance()
                .getAppCompRoot().router.moveToMainActivity(this@SplashActivity)

            val analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
            analyticsTracker.trackAppLaunch()

            finish()

        } else {
            DoubtlessApp.getInstance()
                .getAppCompRoot().router.moveToLoginActivity(this@SplashActivity)

            val analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
            analyticsTracker.trackAppLaunch()

            finish()
        }
    }

    private fun checkMinAppVersionRequired() {
        val currVersion = BuildConfig.VERSION_CODE
        val remoteConfig = DoubtlessApp.getInstance().getAppCompRoot().getRemoteConfig()

        var minVersion = 0

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                minVersion = remoteConfig.getLong("min_app_version").toInt();
                if (minVersion > currVersion) {
                    showAppUpdateDialog()
                } else {
                    moveToNextActivity()
                }
            }
        }
    }

    private fun showAppUpdateDialog() {
        val dialog = AlertDialog.Builder(this).apply {
            title = "New Version Available"
            setMessage("Please update the app to continue")
            setPositiveButton("Update") { _, _ ->
                val playStoreUrl = "https://play.google.com/store/apps/details?id=com.doubtless.doubtless"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Play Store Not Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}