package com.sarftec.messiwallpapers.data.firebase.repository.wallpaper

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sarftec.messiwallpapers.data.DATA_PAGE_SIZE
import com.sarftec.messiwallpapers.data.firebase.extra.FirebaseKey
import com.sarftec.messiwallpapers.data.firebase.extra.FirebaseResult
import com.sarftec.messiwallpapers.data.firebase.mapper.FirebaseWallpaperMapper
import com.sarftec.messiwallpapers.data.firebase.model.FirebaseWallpaper
import kotlinx.coroutines.tasks.await

abstract class FirebaseBaseRepository(
    mapper: FirebaseWallpaperMapper
) : FirebaseBuildWallpaperRepository(mapper) {

    abstract fun getQuery(): Query

    open suspend fun loadFirstPage(key: FirebaseKey.ID): FirebaseResult {
        return if (!FirebaseKey.isDefaultKey(key)) selectedFirstPage(key)
        else defaultFirstPage(key)
    }

    suspend fun getWallpapers(key: FirebaseKey): FirebaseResult {
        return when (key.direction) {
            FirebaseKey.Direction.NEXT -> getNextWallpapers(key)
            FirebaseKey.Direction.PREVIOUS -> {
                getPreviousWallpapers(key)
            }
        }
    }

    private suspend fun selectedFirstPage(key: FirebaseKey.ID): FirebaseResult {
        val documentSnapshot = getDocumentSnapshot(key.id)
        val snapshot = getQuery()
            .orderBy(FirebaseWallpaper.FIELD_ID, Query.Direction.DESCENDING)
            .startAfter(documentSnapshot)
            .limit(DATA_PAGE_SIZE - 1)
            .get()
            .await()
        val wallpapers = snapshot.documents.map { it as DocumentSnapshot }
            .toMutableList()
            .also { it.add(0, documentSnapshot) }
            .mapNotNull { docSnapshot ->
                docSnapshot.toObject(FirebaseWallpaper::class.java).also {
                    it?.id = documentSnapshot.id.toLong()
                }
            }
        return FirebaseResult(
            wallpapers.map { mapper.toMessiWallpaper(it) },
            snapshot.documents.lastOrNull()?.let {
                FirebaseKey.Snapshot(FirebaseKey.Direction.NEXT, it)
            },
            null//FirebaseKey.ID(FirebaseKey.Direction.PREVIOUS, key.id)
        )
    }

    private suspend fun defaultFirstPage(key: FirebaseKey.ID): FirebaseResult {
        val snapshot = getQuery()
            .orderBy(FirebaseWallpaper.FIELD_ID, Query.Direction.DESCENDING)
            .limit(DATA_PAGE_SIZE)
            .get()
            .await()
        val wallpapers = snapshot.map { documentSnapshot ->
            documentSnapshot.toObject(FirebaseWallpaper::class.java).also {
                it.id = documentSnapshot.id.toLong()
            }
        }
        return FirebaseResult(
            wallpapers.map { mapper.toMessiWallpaper(it) },
            snapshot.documents.lastOrNull()?.let {
                FirebaseKey.Snapshot(FirebaseKey.Direction.NEXT, it)
            },
            null
        )
    }

    private suspend fun getNextWallpapers(
        snapshot: DocumentSnapshot,
        key: FirebaseKey
    ): FirebaseResult {
        val querySnapshots = querySnapshot(snapshot, Query.Direction.DESCENDING)
        val wallpapers = querySnapshots.map { documentSnapshot ->
            documentSnapshot.toObject(FirebaseWallpaper::class.java).also {
                it.id = documentSnapshot.id.toLong()
            }
        }
        return FirebaseResult(
            data = wallpapers.map { mapper.toMessiWallpaper(it) },
            nextKey = querySnapshots.lastOrNull()?.let {
                FirebaseKey.Snapshot(FirebaseKey.Direction.NEXT, it)
            },
            previousKey = key
        )
    }

    private suspend fun getNextWallpapers(key: FirebaseKey): FirebaseResult {
        return when (key) {
            is FirebaseKey.Snapshot -> {
                getNextWallpapers(key.ref, key)
            }
            is FirebaseKey.ID -> {
                val snapshot = getDocumentSnapshot(key.id)
                getNextWallpapers(snapshot, key)
            }
        }
    }

    private suspend fun getPreviousWallpapers(
        snapshot: DocumentSnapshot,
        key: FirebaseKey
    ): FirebaseResult {
        val querySnapshots = querySnapshot(snapshot, Query.Direction.ASCENDING)
        val wallpapers = querySnapshots.map { documentSnapshot ->
            documentSnapshot.toObject(FirebaseWallpaper::class.java).also {
                it.id = documentSnapshot.id.toLong()
            }
        }
        return FirebaseResult(
            data = wallpapers.reversed().map { mapper.toMessiWallpaper(it) },
            nextKey = FirebaseKey.ID(
                FirebaseKey.Direction.NEXT,
                querySnapshots.first().id.toLong()
            ),
            previousKey = querySnapshots.lastOrNull()?.let {
                FirebaseKey.Snapshot(FirebaseKey.Direction.PREVIOUS, it)
            }
        )
    }

    private suspend fun getPreviousWallpapers(key: FirebaseKey): FirebaseResult {
        return when (key) {
            is FirebaseKey.Snapshot -> {
                getPreviousWallpapers(key.ref, key).also {
                    Log.v("TAG", "Previous Snapshot Items size => ${it.data.size}")
                }
            }
            is FirebaseKey.ID -> {
                val snapshot = getDocumentSnapshot(key.id)
                getPreviousWallpapers(snapshot, key).also {
                    Log.v("TAG", "Previous ID Items size => ${it.data.size}")
                }
            }
        }
    }

    private suspend fun querySnapshot(
        snapshot: DocumentSnapshot,
        direction: Query.Direction
    ): QuerySnapshot {
        return getQuery()
            .orderBy(FirebaseWallpaper.FIELD_ID, direction)
            .startAfter(snapshot)
            .limit(DATA_PAGE_SIZE)
            .get()
            .await()
    }

    private suspend fun getDocumentSnapshot(id: Long): DocumentSnapshot {
        return collectionRef
            .document(id.toString())
            .get()
            .await()
    }
}