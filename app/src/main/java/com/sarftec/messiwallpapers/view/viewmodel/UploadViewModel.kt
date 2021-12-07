package com.sarftec.messiwallpapers.view.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.usecase.image.UploadImage
import com.sarftec.messiwallpapers.domain.usecase.wallpaper.CreateWallpaper
import com.sarftec.messiwallpapers.utils.Event
import com.sarftec.messiwallpapers.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadImage: UploadImage,
    private val createWallpaper: CreateWallpaper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uploadWallpapers = MutableLiveData<List<UploadInfoOverlay>>()
    val uploadWallpapers: LiveData<List<UploadInfoOverlay>>
        get() = _uploadWallpapers

    private val _updateAdapterPosition = MutableLiveData<Event<Int>>()
    val updateAdapterPosition: LiveData<Event<Int>>
        get() = _updateAdapterPosition

    private fun updateAdapterPosition(uploadInfo: UploadInfo) {
        _uploadWallpapers.value?.let { list ->
            list.indexOfFirst { it.imageInfo == uploadInfo.infoOverlay.imageInfo }
        }?.let { _updateAdapterPosition.value = Event(it) }
    }

    suspend fun uploadWallpaper(uploadInfo: UploadInfo): Resource<Unit> {
        if (uploadInfo.infoOverlay.isUploaded) return Resource.success(Unit)
        createWallpaper(uploadInfo).let {
            if (it.isError()) return Resource.error("${it.message}")
        }
        createImage(uploadInfo.infoOverlay.imageInfo).let {
            if (it.isError()) return Resource.error("${it.message}")
        }
        withContext(Dispatchers.Main) {
            uploadInfo.infoOverlay.isUploaded = true
            updateAdapterPosition(uploadInfo)
        }
        return Resource.success(Unit)
    }

    private fun createTempFile(imageInfo: ImageInfo): File {
        val stream = context.contentResolver.openInputStream(imageInfo.uri)
            ?: throw Exception("Cannot open input stream => ${imageInfo.uri}")
        return File.createTempFile(
            imageInfo.name,
            ".${imageInfo.extension}",
            context.cacheDir
        ).also { file ->
            file.outputStream().use {
                stream.copyTo(it)
            }
        }
    }

    private suspend fun createImage(imageInfo: ImageInfo): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                uploadImage.execute(
                    UploadImage.UploadParam(
                        createTempFile(imageInfo),
                        imageInfo
                    )
                ).result
            } catch (e: Exception) {
                Resource.error(e.message)
            }
        }
    }

    private suspend fun createWallpaper(uploadInfo: UploadInfo): Resource<Unit> {
        val section = when (uploadInfo.section) {
            "Popular" -> CreateWallpaper.Section.POPULAR
            "Barcelona" -> CreateWallpaper.Section.BARCA
            "Paris Saint Germain" -> CreateWallpaper.Section.PSG
            "Argentina" -> CreateWallpaper.Section.ARGENTINA
            else -> return Resource.error("Error => section should either be RECENT / POPULAR")
        }
        val wallpaperResult = createWallpaper.execute(
            CreateWallpaper.CreateParam(
                section,
                uploadInfo.infoOverlay.imageInfo,
            )
        )
        return if (wallpaperResult.result.isSuccess()) Resource.success(Unit)
        else Resource.error(wallpaperResult.result.message)
    }

    fun setUploadWallpapers(images: List<ImageInfo>) {
        _uploadWallpapers.value = images.map {
            UploadInfoOverlay(it)
        }
    }

    fun clearUploadWallpapers() {
        _uploadWallpapers.value = emptyList()
    }

    fun getUploadWallpapers(): List<UploadInfoOverlay> {
        return _uploadWallpapers.value?.filter { !it.isUploaded }
            ?: emptyList()
    }

    class UploadInfo(val section: String, val infoOverlay: UploadInfoOverlay)

    class UploadInfoOverlay(val imageInfo: ImageInfo, var isUploaded: Boolean = false)
}