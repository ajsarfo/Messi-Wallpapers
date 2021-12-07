package com.sarftec.messiwallpapers.data.firebase.extra

import com.sarftec.messiwallpapers.domain.model.MessiWallpaper

class FirebaseResult(
    val data: List<MessiWallpaper>,
    var nextKey: FirebaseKey? = null,
    var previousKey: FirebaseKey? = null
)