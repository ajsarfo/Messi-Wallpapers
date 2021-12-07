package com.sarftec.messiwallpapers.view.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.databinding.ActivityFavoriteBinding
import com.sarftec.messiwallpapers.view.adapter.WallpaperFavoriteAdapter
import com.sarftec.messiwallpapers.view.advertisement.BannerManager
import com.sarftec.messiwallpapers.view.dialog.LoadingDialog
import com.sarftec.messiwallpapers.view.dialog.SetWallpaperDialog
import com.sarftec.messiwallpapers.view.handler.ReadWriteHandler
import com.sarftec.messiwallpapers.view.handler.ToolingHandler
import com.sarftec.messiwallpapers.view.utils.downloadGlideImage
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.viewmodel.FavoriteViewModel
import com.sarftec.messiwallpapers.view.viewpager.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteActivity : BaseActivity() {

    private val layoutBinding by lazy {
       ActivityFavoriteBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<FavoriteViewModel>()

    private val favoriteAdapter by lazy {
        WallpaperFavoriteAdapter(lifecycleScope, viewModel)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        /*************** Admob Configuration ********************/
        BannerManager(this, adRequestBuilder).attachBannerAd(
            getString(R.string.admob_banner_favorite),
            layoutBinding.mainBanner
        )
        /**********************************************************/
        readWriteHandler = ReadWriteHandler(this)
        viewModel.loadWallpapers()
        setupClickListeners()
        setupViewPager()
        observeFavorites()
    }

    private fun runCurrentBitmapCallback(callback: (Bitmap) -> Unit) {
        loadingDialog.show()
        viewModel.getAtPosition(layoutBinding.viewPager.currentItem)?.let { image ->
            lifecycleScope.launch {
                viewModel.getImage(image).let {
                    if(it.isSuccess()) this@FavoriteActivity.downloadGlideImage(it.data!!).let { result ->
                        if(result.isSuccess()) callback(result.data!!)
                        else toast("Action Failed!")
                    }
                    else toast("Action Failed!")
                    loadingDialog.dismiss()
                    //  if (it.isSuccess()) callback(it.data!!)
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModel.wallpapers.observe(this) {
            showLayout(!it.isLoading())
            if(it.isSuccess()) {
                if (it.data!!.isEmpty()) showNoFavoriteText()
                favoriteAdapter.submitData(it.data)
            }
        }
    }

    private fun setupClickListeners() {
        layoutBinding.back.setOnClickListener { onBackPressed() }
        layoutBinding.share.setOnClickListener {
            runCurrentBitmapCallback { toolingHandler.shareImage(it) }
        }
        layoutBinding.wallpaper.setOnClickListener {
            wallpaperDialog.show()
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
        layoutBinding.viewPager.adapter = favoriteAdapter
        layoutBinding.viewPager.setPageTransformer(
            ZoomOutPageTransformer()
        )
        layoutBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.getAtPosition(position)?.let {
                        setFavorite(it.wallpaper.isFavorite)
                    }
                    super.onPageSelected(position)
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

    private fun showNoFavoriteText() {
        layoutBinding.apply {
            detailLayout.visibility = View.GONE
            circularProgress.visibility = View.GONE
            noFavoriteText.visibility = View.VISIBLE
        }
    }

    private fun showLayout(isShown: Boolean) {
        layoutBinding.detailLayout.visibility = if (isShown) View.VISIBLE else View.GONE
        layoutBinding.circularProgress.visibility = if (isShown) View.GONE else View.VISIBLE
    }
}