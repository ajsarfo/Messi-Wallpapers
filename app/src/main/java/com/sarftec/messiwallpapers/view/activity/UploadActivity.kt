package com.sarftec.messiwallpapers.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.messiwallpapers.R
import com.sarftec.messiwallpapers.databinding.ActivityUploadBinding
import com.sarftec.messiwallpapers.utils.Resource
import com.sarftec.messiwallpapers.view.adapter.WallpaperUploadAdapter
import com.sarftec.messiwallpapers.view.dialog.ProgressDialog
import com.sarftec.messiwallpapers.view.handler.FetchPictureHandler
import com.sarftec.messiwallpapers.view.handler.ReadWriteHandler
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.viewmodel.UploadViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UploadActivity : BaseActivity() {

    private val layoutBinding by lazy {
        ActivityUploadBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var fetchPictureHandler: FetchPictureHandler

    private lateinit var readWriteHandler: ReadWriteHandler

    private val viewModel by viewModels<UploadViewModel>()

    private val uploadAdapter by lazy {
        WallpaperUploadAdapter(lifecycleScope, viewModel)
    }

    private val wallpaperSections by lazy {
        resources.getStringArray(R.array.wallpapers_sections)
    }

    private var uploadJob: Job? = null

    private val uploadDialog by lazy {
        ProgressDialog(
            layoutBinding.root,
            this,
            onCancel = {
                uploadJob = null
                toast("Upload failed")
            },
            onFinished = {
                uploadJob = null
                toast("Upload success")
            }
        )
    }

    override fun canShowInterstitial(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutBinding.root)
        readWriteHandler = ReadWriteHandler(this)
        fetchPictureHandler = FetchPictureHandler(this)
        setupAdapter()
        setupSpinners()
        setupButtons()
        layoutBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
        viewModel.uploadWallpapers.observe(this) {
            uploadAdapter.submitData(it)
            setButtonContainerVisibility(it.isNotEmpty())
        }
        viewModel.updateAdapterPosition.observe(this) { event ->
            event.getIfNotHandled()?.let {
                uploadAdapter.notifyItemChanged(it)
            }
        }
    }

    private fun getWallpapersFromDevice() {
        fetchPictureHandler.getImagesFromDevice {
            viewModel.setUploadWallpapers(it)
        }
    }

    private fun setButtonContainerVisibility(isVisible: Boolean) {
        layoutBinding.buttonsContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
        layoutBinding.loadWallpapers.visibility = if (isVisible) View.GONE else View.VISIBLE
        layoutBinding.uploadText.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private suspend fun uploadWallpaper(
        overlay: UploadViewModel.UploadInfoOverlay
    ): Resource<Unit> {
        return viewModel.uploadWallpaper(
            UploadViewModel.UploadInfo(
                wallpaperSections[layoutBinding.sectionSpinner.selectedIndex],
                overlay
            )
        )
    }

    private fun uploadWallpapers(items: List<UploadViewModel.UploadInfoOverlay>) {
        if(items.isEmpty()) {
            uploadDialog.dismiss()
            return
        }
        val progressDiff = 1 / items.size.toFloat()
        uploadJob = lifecycleScope.launch {
            val tasks = items.map { overlay ->
                async(Dispatchers.IO) {
                    uploadWallpaper(overlay)
                }
            }
            tasks.forEachIndexed { index, deferred ->
                val result = deferred.await()
                if (result.isError()) Log.v("TAG", "Error => ${result.message}")
                if (result.isSuccess()) {
                    uploadDialog.setProgress((progressDiff * (index + 1) * 100).toInt())
                }
            }
        }
    }

    private fun setupButtons() {
        layoutBinding.loadWallpapers.setOnClickListener {
            getWallpapersFromDevice()
        }
        layoutBinding.upload.setOnClickListener {
            readWriteHandler.requestReadWrite {
                uploadDialog.show()
                uploadWallpapers(viewModel.getUploadWallpapers())
            }
        }
        layoutBinding.clear.setOnClickListener {
            viewModel.clearUploadWallpapers()
        }
    }

    private fun setupSpinners() {
        layoutBinding.sectionSpinner.setBackgroundColor(
            ContextCompat.getColor(this, R.color.upload_spinner_background)
        )
        layoutBinding.sectionSpinner.setTextColor(
            ContextCompat.getColor(this, R.color.upload_spinner_text_color)
        )
        layoutBinding.sectionSpinner.setItems(*wallpaperSections)
    }

    private fun setupAdapter() {
        layoutBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                this@UploadActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = uploadAdapter
        }
    }
}