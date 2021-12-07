package com.sarftec.messiwallpapers.data.firebase.repository.wallpaper

import com.google.firebase.firestore.Query
import com.sarftec.messiwallpapers.data.firebase.mapper.FirebaseWallpaperMapper
import com.sarftec.messiwallpapers.data.firebase.model.FirebaseWallpaper

class FirebaseWallpaperRepository(
    private val section: String,
    mapper: FirebaseWallpaperMapper
    ) : FirebaseBaseRepository(mapper) {

    override fun getQuery(): Query {
        return collectionRef.whereEqualTo(FirebaseWallpaper.FIELD_SECTION, section)
            //.whereEqualTo(FirebaseWallpaper.FIELD_IS_APPROVED, true)
    }
}