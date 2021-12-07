package com.sarftec.messiwallpapers.domain.usecase.header

import android.net.Uri
import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class GetHeader @Inject constructor(
    private val imageRepository: ImageRepository
) : UseCase<GetHeader.HeaderParam, GetHeader.HeaderResult>() {

    override suspend fun execute(param: HeaderParam?): HeaderResult {
      if(param == null) return HeaderResult(Resource.error("Header param NULL!"))
        return HeaderResult(imageRepository.getImageUri(HEADER_IMAGE_LOCATION))
    }

    object HeaderParam : Param
    class HeaderResult(val result: Resource<Uri>) : Response

    companion object {
        private const val HEADER_IMAGE_LOCATION = "header/header.jpg"
    }
}