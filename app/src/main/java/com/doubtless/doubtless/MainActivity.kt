package com.doubtless.doubtless

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.doubtless.doubtless.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarColor = Color.BLACK

        binding.retroBtn.setOnClickListener {
            binding.progress.isVisible = !binding.progress.isVisible
        }

//        val email = intent.getStringExtra("email")
//        val name = intent.getStringExtra("name")
//
//
//        binding.primaryBtn.text= email + "\n"+ name
    }
}