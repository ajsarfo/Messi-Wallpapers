package com.sarftec.messiwallpapers.data.repository

import android.net.Uri
import com.sarftec.messiwallpapers.data.cache.UriCache
import com.sarftec.messiwallpapers.data.firebase.repository.FirebaseImageRepository
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.utils.Resource
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
  private val repository: FirebaseImageRepository,
  private val uriCache: UriCache
): ImageRepository {

    override suspend fun uploadWallpaper(file: File, imageInfo: ImageInfo): Resource<Unit> {
        return repository.uploadWallpaper(file, imageInfo.toFullName())
    }

    override suspend fun getImageUri(imageName: String): Resource<Uri> {
        val resource = uriCache.getUri(imageName)
        if(resource.isError()) {
            repository.getImageUri(imageName).let {
                if(it.isSuccess()) uriCache.setUri(imageName, it.data!!)
                return it
            }
        }
      return resource
    }

    override suspend fun deleteImage(imageName: String): Resource<Unit> {
       return repository.deleteImage(imageName)
    }
}