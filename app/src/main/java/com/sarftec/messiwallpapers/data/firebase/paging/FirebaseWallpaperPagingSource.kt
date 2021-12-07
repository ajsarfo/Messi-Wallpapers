package com.sarftec.messiwallpapers.data.firebase.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarftec.messiwallpapers.data.firebase.extra.FirebaseKey
import com.sarftec.messiwallpapers.data.firebase.repository.wallpaper.FirebaseBaseRepository
import com.sarftec.messiwallpapers.domain.model.MessiWallpaper

class FirebaseWallpaperPagingSource(
    private val repository: FirebaseBaseRepository,
    private val startId: Long
) : PagingSource<FirebaseKey, MessiWallpaper>() {

    override fun getRefreshKey(state: PagingState<FirebaseKey, MessiWallpaper>): FirebaseKey? {
        return null
    }

    override suspend fun load(params: LoadParams<FirebaseKey>): LoadResult<FirebaseKey, MessiWallpaper> {
        return try {
            val nextKey = params.key ?: return getFirstPage(startId)
           val result = repository.getWallpapers(nextKey)

            LoadResult.Page(
                data = result.data,
                nextKey = result.nextKey,
                prevKey = result.previousKey
            )
        } catch (e: Exception) {
            Log.v("TAG", "${e.message}")
            LoadResult.Error(e)
        }
    }

    private suspend fun getFirstPage(id: Long): LoadResult<FirebaseKey, MessiWallpaper> {
        return try {
            val key = if (isInitialId(id)) FirebaseKey.getInitialKey()
            else FirebaseKey.ID(FirebaseKey.Direction.NEXT, id)

            val result = repository.loadFirstPage(key)
            LoadResult.Page(
                data = result.data,
                nextKey = result.nextKey,
                prevKey = result.previousKey
            )
        } catch (e: Exception) {
            Log.v("TAG", "${e.message}")
            LoadResult.Error(e)
        }
    }

    companion object {
        fun isInitialId(id: Long): Boolean = id < 0
    }
}