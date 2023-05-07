package com.doubtless.doubtless.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, MainFragment(), null)
                .commit()
        }
    }
}