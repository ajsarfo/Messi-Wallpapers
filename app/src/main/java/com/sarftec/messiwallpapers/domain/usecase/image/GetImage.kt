package com.sarftec.messiwallpapers.domain.usecase.image

import android.net.Uri
import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetImage @Inject constructor(
    private val imageRepository: ImageRepository
) : UseCase<GetImage.GetImageParam, GetImage.GetImageResult>() {

    override suspend fun execute(param: GetImageParam?): GetImageResult {
        if (param == null) GetImageResult(
            Resource.error("Null get image param!")
        )
        return withContext(Dispatchers.IO) {
            GetImageResult(imageRepository.getImageUri(param!!.imageLocation))
        }
    }

    class GetImageParam(val imageLocation: String) : Param
    class GetImageResult(val image: Resource<Uri>) : Response
}