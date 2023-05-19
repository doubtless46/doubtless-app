package com.doubtless.doubtless.screens.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.utils.anims.animateFadeDown
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
            delay(1600L)
            view.animateFadeDown(800L)
        }
    }
}