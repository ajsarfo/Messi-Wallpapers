package com.sarftec.messiwallpapers.domain.model

class MessiWallpaper(
    val id: Long,
    val views: Long,
    val imageLocation: String,
    val section: String,
    var isFavorite: Boolean = false
)