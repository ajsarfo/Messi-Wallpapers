package com.sarftec.messiwallpapers.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.view.advertisement.AdCountManager
import com.sarftec.messiwallpapers.view.advertisement.InterstitialManager
import com.sarftec.messiwallpapers.view.manager.NetworkManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    protected val adRequestBuilder: AdRequest by lazy {
        AdRequest.Builder().build()
    }

    protected var interstitialManager: InterstitialManager? = null

    @Inject
    lateinit var networkManager: NetworkManager

    protected open fun canShowInterstitial() : Boolean = true

    protected open fun createAdCounterManager() : AdCountManager {
        return AdCountManager(listOf(1, 4, 3))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Load interstitial if required by extending activity
        if(!canShowInterstitial()) return
        interstitialManager = InterstitialManager(
            this,
            getString(R.string.admob_interstitial_id),
            networkManager,
            createAdCounterManager(),
            adRequestBuilder
        )
        interstitialManager?.load()
    }

    protected fun <T> navigateTo(
        klass: Class<T>,
        finish: Boolean = false,
        slideIn: Int = R.anim.slide_in_right,
        slideOut: Int = R.anim.slide_out_left,
        bundle: Bundle? = null
    ) {
        val intent = Intent(this, klass).also {
            it.putExtra(ACTIVITY_BUNDLE, bundle)
        }
        startActivity(intent)
        if (finish) finish()
        overridePendingTransition(slideIn, slideOut)
    }

    protected fun <T> navigateToWithParcel(
        klass: Class<T>,
        finish: Boolean = false,
        slideIn: Int = R.anim.slide_in_right,
        slideOut: Int = R.anim.slide_out_left,
        parcel: Parcelable? = null
    ) {
        val intent = Intent(this, klass).also {
            it.putExtra(ACTIVITY_BUNDLE, parcel)
        }
        startActivity(intent)
        if (finish) finish()
        overridePendingTransition(slideIn, slideOut)
    }

    protected fun <T : Parcelable> getParcelFromIntent(intent: Intent) : T? {
        return intent.getParcelableExtra(ACTIVITY_BUNDLE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    protected fun setStatusBarBackgroundLight() {
        fun dayMode() {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    window.decorView.windowInsetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    val view = window.decorView
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
        fun darkMode() {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    window.decorView.windowInsetsController?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    val view = window.decorView
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
        when(resources.configuration.uiMode and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {  }
            Configuration.UI_MODE_NIGHT_NO -> { dayMode() }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {  }
        }
    }


    fun statusColor(color: Int) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = color
            }
        }
    }

    protected fun hideSystemBars() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.systemBars())
            } else {
                var uiVisibility = window.decorView.systemUiVisibility
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
                uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
                //uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
                    uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
                window.decorView.systemUiVisibility = uiVisibility
            }
        } catch (e: Exception) {
            Log.v("TAG", "Cannot hide system bars :=> ${e.message}")
        }
    }

    companion object {
        const val ACTIVITY_BUNDLE = "activity_bundle"
    }
}