package com.sarftec.messiwallpapers.data.cache

import android.net.Uri
import com.sarftec.messiwallpapers.data.room.MessiDatabase
import com.sarftec.messiwallpapers.data.room.model.RoomImage
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class UriCacheImpl @Inject constructor(
    private val appDatabase: MessiDatabase
) : UriCache {

    override suspend fun getUri(id: String): Resource<Uri> {
        return appDatabase.roomImageDao().getImage(id)
            ?.let { Resource.success(it.uri) }
            ?: Resource.error("Error => Image \'$id\' not found in database!")
    }

    override suspend fun setUri(id: String, uri: Uri) {
        appDatabase.roomImageDao().insertImage(
            RoomImage(id, uri)
        )
    }
}