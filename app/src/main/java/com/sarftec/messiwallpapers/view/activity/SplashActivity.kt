package com.sarftec.messiwallpapers.view.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sarftec.messiwallpapers.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val layoutBinding by lazy {
       ActivitySplashBinding.inflate(
           layoutInflater
       )
    }

    override fun canShowInterstitial(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        lifecycleScope.launchWhenCreated {
            delay(TimeUnit.SECONDS.toMillis(3))
            navigateTo(MainActivity::class.java, finish = true)
        }
    }
}