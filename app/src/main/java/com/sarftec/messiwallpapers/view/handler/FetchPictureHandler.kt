package com.sarftec.messiwallpapers.view.handler

import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sarftec.messiwallpapers.domain.model.ImageInfo

class FetchPictureHandler(
    private val activity: AppCompatActivity
) {

    private var action: ((List<ImageInfo>) -> Unit)? = null

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.mapNotNull { mapUriToImageInfo(it) }
            .let { action?.invoke(it) }
    }

    fun getImagesFromDevice(action: (List<ImageInfo>) -> Unit) {
        this.action = action
        launcher.launch(arrayOf("image/*"))
    }

    private fun getUriCursor(uri: Uri): Cursor? {
        return activity.contentResolver.query(uri, null, null, null, null)
    }

    private fun mapUriToImageInfo(uri: Uri): ImageInfo? {
        val cursor = getUriCursor(uri) ?: return null
        val fileName = cursor.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(index)
        }
        return ImageInfo(
            uri,
            fileName.substringBeforeLast("."),
            fileName.substringAfterLast(".", "")
        )
    }
}