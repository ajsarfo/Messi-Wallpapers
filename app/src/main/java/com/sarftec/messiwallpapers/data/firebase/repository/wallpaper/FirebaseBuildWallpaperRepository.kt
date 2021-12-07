package com.sarftec.messiwallpapers.data.firebase.repository.wallpaper

import com.google.firebase.firestore.FirebaseFirestore
import com.sarftec.messiwallpapers.data.FIREBASE_WALLPAPER_FOLDER
import com.sarftec.messiwallpapers.data.firebase.mapper.FirebaseWallpaperMapper
import com.sarftec.messiwallpapers.data.firebase.model.FirebaseWallpaper
import com.sarftec.messiwallpapers.domain.model.ImageInfo
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper
import com.sarftec.messiwallpapers.domain.repository.WallpaperRepository
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

open class FirebaseBuildWallpaperRepository @Inject constructor(
    protected val mapper: FirebaseWallpaperMapper
) {

    protected val firebaseRef = FirebaseFirestore.getInstance()

    protected val collectionRef = firebaseRef.collection("wallpapers")

    suspend fun createWallpaper(
        section: WallpaperRepository.Section,
        imageInfo: ImageInfo
    ): Resource<MessiWallpaper> {
        return try {
            val wallpaper = FirebaseWallpaper()
                .also {
                    it.id = Date().time
                    it.views = (800L until 4000L).random()
                    it.image = "$FIREBASE_WALLPAPER_FOLDER/${imageInfo.toFullName()}"
                    it.section = section.id
                }
            collectionRef.document(wallpaper.id.toString())
                .set(wallpaper)
                .await()
            Resource.success(mapper.toMessiWallpaper(wallpaper))
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }

    suspend fun deleteWallpaper(firebaseWallpaper: FirebaseWallpaper): Resource<Unit> {
        return try {
            collectionRef.document(firebaseWallpaper.id.toString())
                .delete()
                .await()
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }

    suspend fun increaseViews(firebaseWallpaper: FirebaseWallpaper): Resource<Unit> {
        return try {
            firebaseRef.runTransaction { transaction ->
                val docRef = collectionRef.document(firebaseWallpaper.id!!.toString())
                val newViews = transaction.get(docRef)
                    .getLong(FirebaseWallpaper.FIELD_VIEWS)
                    ?.plus(1)
                transaction.update(docRef, FirebaseWallpaper.FIELD_VIEWS, newViews)
            }
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }
}