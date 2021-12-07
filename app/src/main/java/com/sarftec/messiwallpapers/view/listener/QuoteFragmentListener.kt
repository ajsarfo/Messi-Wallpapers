package com.sarftec.messiwallpapers.view.listener

import com.sarftec.messiwallpapers.view.advertisement.InterstitialManager
import com.sarftec.messiwallpapers.view.advertisement.RewardVideoManager
import com.sarftec.messiwallpapers.view.handler.ReadWriteHandler

interface QuoteFragmentListener {
    fun getReadWriteHandler() : ReadWriteHandler
    fun showLoadingDialog(isShown: Boolean)
    fun getRewardVideo() : RewardVideoManager
    fun getAdInterstitialManager() : InterstitialManager?
}