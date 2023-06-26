package com.doubtless.doubtless.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.doubtless.doubtless.R
import com.doubtless.doubtless.databinding.ActivityMainBinding
import com.doubtless.doubtless.navigation.BackPressDispatcher
import com.doubtless.doubtless.navigation.OnBackPressListener

class MainActivity : AppCompatActivity(), BackPressDispatcher {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, MainFragment(), "MainFragment")
                .commit()
        }
    }

    fun getMainFragment(): MainFragment? {
        return supportFragmentManager.findFragmentByTag("MainFragment") as MainFragment?
    }

    // ----- backpress impl ------

    private val backPressListeners: MutableList<OnBackPressListener> = mutableListOf()

    override fun registerBackPress(listener: OnBackPressListener) {
        if (!backPressListeners.contains(listener))
            backPressListeners.add(listener)
    }

    override fun unregisterBackPress(listener: OnBackPressListener) {
        if (backPressListeners.contains(listener))
            backPressListeners.remove(listener)
    }

    override fun onBackPressed() {
        var backPressConsumed = false

        for (backPressListener in backPressListeners) {
            if (backPressListener.onBackPress()) {
                backPressConsumed = true
            }
        }

        if (!backPressConsumed)
            super.onBackPressed()
    }

}