package com.sarftec.messiwallpapers.domain.usecase.image

import com.sarftec.messiwallpapers.domain.repository.ImageRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteImage @Inject constructor(
    private val imageRepository: ImageRepository
) : UseCase<DeleteImage.DeleteParam, DeleteImage.DeleteResult>() {

    override suspend fun execute(param: DeleteParam?): DeleteResult {
        if (param == null) return DeleteResult(
            Resource.error("Delete image param null!")
        )
        return withContext(Dispatchers.IO) {
            DeleteResult(imageRepository.deleteImage(param.imageLocation))
        }
    }

    class DeleteParam(
        val imageLocation: String
    ) : Param

    class DeleteResult(
        val result: Resource<Unit>
    ) : Response
}