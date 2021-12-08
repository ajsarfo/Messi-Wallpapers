package com.sarftec.messiwallpapers.view.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.databinding.ActivityDetailBinding
import com.sarftec.messiwallpapers.view.adapter.WallpaperDetailAdapter
import com.sarftec.messiwallpapers.view.advertisement.AdCountManager
import com.sarftec.messiwallpapers.view.advertisement.BannerManager
import com.sarftec.messiwallpapers.view.advertisement.RewardVideoManager
import com.sarftec.messiwallpapers.view.dialog.LoadingDialog
import com.sarftec.messiwallpapers.view.dialog.ScrimDialog
import com.sarftec.messiwallpapers.view.dialog.SetWallpaperDialog
import com.sarftec.messiwallpapers.view.handler.ReadWriteHandler
import com.sarftec.messiwallpapers.view.handler.ToolingHandler
import com.sarftec.messiwallpapers.view.parcel.WallpaperToDetail
import com.sarftec.messiwallpapers.view.utils.downloadGlideImage
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.viewmodel.DetailViewModel
import com.sarftec.messiwallpapers.view.viewpager.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : BaseActivity() {

    private val layoutBinding by lazy {
        ActivityDetailBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel by viewModels<DetailViewModel>()

    private val detailAdapter by lazy {
        WallpaperDetailAdapter(lifecycleScope, viewModel)
    }

    private lateinit var readWriteHandler: ReadWriteHandler

    private val toolingHandler by lazy {
        ToolingHandler(this, readWriteHandler)
    }

    private val loadingDialog by lazy {
        LoadingDialog(this, layoutBinding.root)
    }

    private val wallpaperDialog by lazy {
        SetWallpaperDialog(
            layoutBinding.root,
            onHome = {
                runCurrentBitmapCallback {
                    toolingHandler.setWallpaper(it, ToolingHandler.WallpaperOption.HOME)
                }
            },
            onLock = {
                runCurrentBitmapCallback {
                    toolingHandler.setWallpaper(it, ToolingHandler.WallpaperOption.LOCK)
                }
            }
        )
    }

    private val rewardVideoManager by lazy {
        RewardVideoManager(
            this,
            R.string.detail_admob_reward_video_id,
            adRequestBuilder,
            networkManager
        )
    }

    override fun createAdCounterManager(): AdCountManager {
        return AdCountManager(listOf(3, 5, 8, 12))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        /*************** Admob Configuration ********************/
        BannerManager(this, adRequestBuilder).attachBannerAd(
            getString(R.string.admob_banner_detail),
            layoutBinding.mainBanner
        )
        /**********************************************************/
        readWriteHandler = ReadWriteHandler(this)
        setupClickListeners()
        setupViewPager()
        setupLoadingIndicator()
        getParcelFromIntent<WallpaperToDetail>(intent)?.let {
            viewModel.setParcel(it)
        }
        viewModel.loadWallpaperFlow()
        observeFlow()
    }

    private fun observeFlow() {
        viewModel.wallpaperFlow.observe(this) { resources ->
            if (resources.isLoading()) showLayout(false)
            if (resources.isError()) Log.v("TAG", "${resources.message}")
            if (resources.isSuccess()) resources.data?.let { flow ->
                lifecycleScope.launchWhenCreated {
                    flow.collect {
                        detailAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun runCurrentBitmapCallback(callback: (Bitmap) -> Unit) {
        loadingDialog.show()
        viewModel.getAtPosition(layoutBinding.viewPager.currentItem)?.let { image ->
            lifecycleScope.launch {
                viewModel.getImage(image).let {
                    if(it.isSuccess()) this@DetailActivity.downloadGlideImage(it.data!!).let { result ->
                        if(result.isSuccess()) {
                            rewardVideoManager.showRewardVideo {
                                loadingDialog.dismiss()
                                callback(result.data!!)
                            }
                        }
                        else {
                            loadingDialog.dismiss()
                            toast("Action Failed!")
                        }
                    }
                    else {
                        loadingDialog.dismiss()
                        toast("Action Failed!")
                    }
                    //  if (it.isSuccess()) callback(it.data!!)
                }
            }
        }
    }


    private fun setupClickListeners() {
        layoutBinding.back.setOnClickListener { onBackPressed() }
        layoutBinding.share.setOnClickListener {
            runCurrentBitmapCallback { toolingHandler.shareImage(it) }
        }
        layoutBinding.download.setOnClickListener {
          runCurrentBitmapCallback { toolingHandler.saveImage(it) }
        }
        layoutBinding.wallpaper.setOnClickListener {
          runCurrentBitmapCallback { wallpaperDialog.show() }
        }
        layoutBinding.favorite.setOnClickListener {
            viewModel.getAtPosition(layoutBinding.viewPager.currentItem)?.let {
                it.wallpaper.isFavorite = !it.wallpaper.isFavorite
                setFavorite(it.wallpaper.isFavorite)
                /*
                if(it.wallpaper.isFavorite) viewModel.saveFavoriteWallpaper(it)
                else viewModel.removeFavoriteWallpaper(it)
                 */
            }
        }
    }

    private fun setupViewPager() {
        layoutBinding.viewPager.adapter = detailAdapter
        layoutBinding.viewPager.setPageTransformer(
            ZoomOutPageTransformer()
        )
        layoutBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    interstitialManager?.showAd {
                        viewModel.getAtPosition(position)?.let {
                            setFavorite(it.wallpaper.isFavorite)
                        }
                    }
                }
            }
        )
    }

    private fun setFavorite(isFavorite: Boolean) {
        viewModel.geWallpaperAtPosition(layoutBinding.viewPager.currentItem)?.let {
            if(isFavorite) viewModel.saveFavorite(it)
            else viewModel.deleteFavorite(it)
        }
        layoutBinding.apply {
            favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_unfavorite
            )
            favoriteText.text = getString(
                if (isFavorite) R.string.favorite else R.string.un_favorite
            )
        }
    }

    private fun showLayout(isShown: Boolean) {
        layoutBinding.detailLayout.visibility = if (isShown) View.VISIBLE else View.GONE
        layoutBinding.circularProgress.visibility = if (isShown) View.GONE else View.VISIBLE
    }

    private fun setupLoadingIndicator() {
        detailAdapter.addLoadStateListener {
            showLayout(
                it.refresh != LoadState.Loading
            )
        }
    }
}