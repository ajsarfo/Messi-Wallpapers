package com.sarftec.messiwallpapers.domain.usecase.image

import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class UploadImage @Inject constructor(
    private val imageRepository: ImageRepository
): UseCase<UploadImage.UploadParam, UploadImage.UploadResult>()  {

    override suspend fun execute(param: UploadParam?): UploadResult {
        if(param == null) return UploadResult(
            Resource.error("Null => upload image param!")
        )
        return withContext(Dispatchers.IO) {
            UploadResult(imageRepository.uploadWallpaper(param.file, param.imageInfo))
        }
    }

    class UploadResult(val result: Resource<Unit>) : Response
    class UploadParam(val file: File, val imageInfo: ImageInfo) : Param

}