package com.sarftec.messiwallpapers.view.handler

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.sarftec.messiwallpapers.view.utils.saveBitmapToGallery
import com.sarftec.messiwallpapers.view.utils.shareImage
import com.sarftec.messiwallpapers.view.utils.toast
import com.sarftec.messiwallpapers.view.utils.viewInGallery
import java.io.File

class ToolingHandler(
    private val context: Context,
    private val readWriteHandler: ReadWriteHandler
) {

    fun shareImage(image: Bitmap) {
        val file = File(context.cacheDir, "share_image.jpg")
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        context.shareImage(file)
    }

    @SuppressLint("MissingPermission")
    fun setWallpaper(image: Bitmap, option: WallpaperOption) {
        val manager = WallpaperManager.getInstance(context)
        when (option) {
            WallpaperOption.HOME -> {
                manager.setBitmap(image)
                context.toast("Home screen set!")
            }
            WallpaperOption.LOCK -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    manager.setBitmap(
                        image,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )
                    context.toast("Lock screen set!")
                } else {
                    context.toast("Device does not have support!")
                }
            }
        }
    }

    fun saveImage(image: Bitmap) {
       readWriteHandler.requestReadWrite {
           context.saveBitmapToGallery(image)?.let {
               context.toast("Image Saved!")
               context.viewInGallery(it)
           }
       }
    }

    enum class WallpaperOption {
        LOCK, HOME
    }
}