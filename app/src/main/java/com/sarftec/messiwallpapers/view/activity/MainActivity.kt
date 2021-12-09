package com.sarftec.messiwallpapers.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.databinding.ActivityMainBinding
import com.sarftec.messiwallpapers.view.advertisement.AdCountManager
import com.sarftec.messiwallpapers.view.advertisement.BannerManager
import com.sarftec.messiwallpapers.view.advertisement.InterstitialManager
import com.sarftec.messiwallpapers.view.advertisement.RewardVideoManager
import com.sarftec.messiwallpapers.view.dialog.LoadingDialog
import com.sarftec.messiwallpapers.view.handler.ReadWriteHandler
import com.sarftec.messiwallpapers.view.listener.DrawerFragmentListener
import com.sarftec.messiwallpapers.view.listener.QuoteFragmentListener
import com.sarftec.messiwallpapers.view.listener.WallpaperFragmentListener
import com.sarftec.messiwallpapers.view.model.WallpaperUI
import com.sarftec.messiwallpapers.view.parcel.WallpaperToDetail
import com.sarftec.messiwallpapers.view.utils.moreApps
import com.sarftec.messiwallpapers.view.utils.rateApp
import com.sarftec.messiwallpapers.view.utils.share
import com.sarftec.messiwallpapers.view.viewmodel.MainViewModel
import com.sarftec.messiwallpapers.view.viewmodel.WallpaperViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(),
    DrawerFragmentListener,
    WallpaperFragmentListener,
    QuoteFragmentListener {

    private val layoutBinding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var readWriteHandler: ReadWriteHandler

    private var drawerCallback: (() -> Unit)? = null

    private val loadingDialog by lazy {
        LoadingDialog(this, layoutBinding.root)
    }

    private val rewardVideoManager by lazy {
        RewardVideoManager(
            this,
            R.string.quote_admob_reward_video_id,
            adRequestBuilder,
            networkManager
        )
    }

    override fun createAdCounterManager(): AdCountManager {
        return AdCountManager(listOf(1, 3, 4, 2, 3))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readWriteHandler = ReadWriteHandler(this)
        setContentView(layoutBinding.root)
        setupNavigationDrawer()
        setupNavigationView()
        setupNavigationHeader()
        layoutBinding.bottomNavigation.setupWithNavController(getNavController())
    }

    override fun onBackPressed() {
        if (layoutBinding.navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            layoutBinding.navigationDrawer.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    private fun getNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(
            R.id.nav_container
        ) as NavHostFragment
        return navHost.navController
    }

    private fun setupNavigationHeader() {
        layoutBinding.navigationView
            .getHeaderView(0)
            .findViewById<ImageView>(R.id.header_image)
            ?.let { imageView ->
                lifecycleScope.launchWhenCreated {
                    viewModel.getHeaderImage().let {
                        if (it.isSuccess()) {
                            Glide.with(this@MainActivity)
                                .load(it.data)
                                .into(imageView)
                        }
                        if (it.isError()) Log.v("TAG", "Header Image Error => ${it.message}")
                    }
                }
            }
    }

    private fun setDrawerCallback(callback: () -> Unit) {
        drawerCallback = callback
        layoutBinding.navigationDrawer.closeDrawer(GravityCompat.START)
    }

    private fun setupNavigationView() {
        layoutBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.favorite_wallpaper -> {
                    setDrawerCallback {
                        navigateTo(FavoriteActivity::class.java)
                    }
                    true
                }
                R.id.upload_wallpaper -> {
                    setDrawerCallback {
                        navigateTo(UploadActivity::class.java)
                    }
                    true
                }
                R.id.share_app -> {
                    setDrawerCallback {
                        share(
                            "Hi take a look at this app ${getString(R.string.app_name)}. Its great! and you will like it too.",
                            "Share"
                        )
                    }
                    true
                }

                R.id.rate_app -> {
                    setDrawerCallback {
                        rateApp()
                    }
                    true
                }
                R.id.more_apps -> {
                    setDrawerCallback {
                        moreApps()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavigationDrawer() {
        layoutBinding.navigationDrawer.addDrawerListener(
            object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

                override fun onDrawerOpened(drawerView: View) {
                }

                override fun onDrawerClosed(drawerView: View) {
                    drawerCallback?.invoke()
                    drawerCallback = null
                }

                override fun onDrawerStateChanged(newState: Int) {}
            }
        )
    }

    override fun openNavDrawer() {
        layoutBinding.navigationDrawer.openDrawer(GravityCompat.START)
    }

    override fun navigateToDetail(
        section: WallpaperViewModel.Section,
        wallpaper: WallpaperUI.Wallpaper
    ) {
        navigateToWithParcel(
            DetailActivity::class.java,
            parcel = WallpaperToDetail(
                wallpaper.wallpaper.id,
                getParcelSelection(section)
            )
        )
    }

    private fun getParcelSelection(selection: WallpaperViewModel.Section): Int {
        return when (selection) {
            WallpaperViewModel.Section.POPULAR -> WallpaperToDetail.POPULAR
            WallpaperViewModel.Section.PSG -> WallpaperToDetail.PSG
            WallpaperViewModel.Section.BARCA -> WallpaperToDetail.BARCA
            WallpaperViewModel.Section.ARGENTINA -> WallpaperToDetail.ARGENTINA
        }
    }

    override fun getReadWriteHandler(): ReadWriteHandler {
        return readWriteHandler
    }

    override fun showLoadingDialog(isShown: Boolean) {
        if (isShown) loadingDialog.show()
        else loadingDialog.dismiss()
    }

    override fun getRewardVideo(): RewardVideoManager {
        return rewardVideoManager
    }

    override fun getAdInterstitialManager(): InterstitialManager? {
        return interstitialManager
    }
}