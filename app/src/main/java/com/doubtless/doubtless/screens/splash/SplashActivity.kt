package com.doubtless.doubtless.screens.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.utils.anims.animateFadeUp
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.Main.immediate).launch {
            val view = findViewById<TextView>(R.id.tv_title)
            delay(200L)
            view.animateFadeUp(800L)

            val userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()

            if (userManager.getLoggedInUser() != null) {
                DoubtlessApp.getInstance()
                    .getAppCompRoot().router.moveToMainActivity(this@SplashActivity)

                val analyticsTracker =
                    DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
                analyticsTracker.trackAppLaunch()

                finish()
            } else {
                DoubtlessApp.getInstance()
                    .getAppCompRoot().router.moveToLoginActivity(this@SplashActivity)

                val analyticsTracker =
                    DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
                analyticsTracker.trackAppLaunch()

                finish()
            }
        }
    }
}