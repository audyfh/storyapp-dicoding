package com.example.storyapp.presentation.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.MainActivity
import com.example.storyapp.databinding.ActivitySplashBinding
import com.example.storyapp.presentation.auth.LoginActivity
import com.example.storyapp.util.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        checkLogin()
    }

    private fun playAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f, 0f, 1f).apply {
            duration = 1000L
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        animator.start()
    }

    private fun checkLogin(){
        lifecycleScope.launch {
            delay(5000)
            val token = PreferencesManager.getToken(this@SplashActivity)
            if (token.isNullOrEmpty()){
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}