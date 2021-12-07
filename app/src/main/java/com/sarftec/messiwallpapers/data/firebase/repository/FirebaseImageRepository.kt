package com.sarftec.messiwallpapers.data.firebase.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.sarftec.messiwallpapers.data.FIREBASE_WALLPAPER_FOLDER
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class FirebaseImageRepository @Inject constructor()  {

    private val ref = FirebaseStorage.getInstance().reference

    suspend fun getImageUri(imageName: String) : Resource<Uri> {
        return try {
            val uri = ref.child(imageName)
                .downloadUrl
                .await()
            Resource.success(uri)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }

    suspend fun uploadWallpaper(file: File, imageName: String) : Resource<Unit> {
       return try {
           ref.child("$FIREBASE_WALLPAPER_FOLDER/$imageName")
               .putBytes(file.readBytes())
               .await()
           Resource.success(Unit)
       } catch (e: Exception) {
           Resource.error("${e.message}")
       }
    }

    suspend fun deleteImage(imageName: String): Resource<Unit> {
        return try {
            ref.child(imageName)
                .delete()
                .await()
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }
}