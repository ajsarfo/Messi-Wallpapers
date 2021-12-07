package com.sarftec.messiwallpapers.data.cache

import android.net.Uri
import com.sarftec.messiwallpapers.utils.Resource

interface UriCache {
    suspend fun getUri(id: String) : Resource<Uri>
    suspend fun setUri(id: String, uri: Uri)
}