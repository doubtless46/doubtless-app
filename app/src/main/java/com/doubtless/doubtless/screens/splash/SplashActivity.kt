package com.doubtless.doubtless.screens.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.utils.anims.animateFadeUp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.*

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
        if (userManager.getLoggedInUser() != null) {
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
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        var minVersion = 0
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()
        remoteConfig.setConfigSettingsAsync(configSettings)
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
            setMessage("Please, update app to new version to continue")
            setPositiveButton("Update") { _, _ ->
                moveToNextActivity()
            }
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}