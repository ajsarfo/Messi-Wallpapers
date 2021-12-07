package com.sarftec.messiwallpapers.view.advertisement

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.view.manager.NetworkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RewardVideoManager(
    private val activity: Activity,
    @StringRes private val rewardId: Int,
    private val adRequest: AdRequest,
    private val networkManager: NetworkManager
) {

    private var navigateForward = false

    private var job: Job? = null

    private var hasNetwork = false

    private var cancelTimer = false

    private var onSuccess: (() -> Unit)? = null

    fun showRewardVideo(onSuccess: (() -> Unit)) {
        this.onSuccess = onSuccess
        if (!networkManager.isNetworkAvailable()) {
            onSuccess()
            return
        }
        hasNetwork = true
        launchRewardTimer()
        RewardedAd.load(
            activity,
            activity.getString(rewardId),
            adRequest,
            getRewardListener()
        )
    }

    private fun getRewardListener(): RewardedAdLoadCallback {
        return object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                navigateForward = false
                onSuccess?.invoke()
            }

            override fun onAdLoaded(rewardAd: RewardedAd) {
                navigateForward = false
                rewardAd.fullScreenContentCallback = getContentCallback()
                if (hasNetwork) {
                    rewardAd.show(activity) {
                        Log.v(
                            "TAG",
                            "User rewarded for item type => ${it.type} and amount ${it.amount}"
                        )
                        navigateForward = true
                    }
                }
            }
        }
    }

    private fun launchRewardTimer() {
        cancelTimer = false
        job = (activity as AppCompatActivity).lifecycleScope.launch {
            delay(TimeUnit.SECONDS.toMillis(5))
            hasNetwork = false
            job = null
            if (!cancelTimer) onSuccess?.invoke()
            cancelTimer = true
        }
    }

    private fun getContentCallback(): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                cancelTimer = true
                Log.v("TAG", "Showed full screen video")
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                navigateForward = false
                onSuccess?.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                if (navigateForward) onSuccess?.invoke()
                if (!navigateForward) onSuccess?.invoke()
                navigateForward = false
                cancelTimer = true
            }
        }
    }
}