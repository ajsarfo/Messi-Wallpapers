package com.sarftec.messiwallpapers.view.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.CompletableDeferred

suspend fun Activity.downloadGlideImage(uri: Uri): Resource<Bitmap> {
    val deferred = CompletableDeferred<Resource<Bitmap>>(null)
    Glide.with(this)
        .asBitmap()
        .load(uri)
        .into(
            object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    deferred.complete(Resource.success(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    deferred.complete(Resource.error("Error => Glide image download failed!"))
                }
            }
        )
    return deferred.await()
}