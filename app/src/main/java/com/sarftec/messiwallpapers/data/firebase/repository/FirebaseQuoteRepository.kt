package com.sarftec.messiwallpapers.data.firebase.repository

import com.google.firebase.storage.FirebaseStorage
import com.sarftec.messiwallpapers.data.firebase.model.FirebaseQuote
import com.sarftec.messiwallpapers.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseQuoteRepository @Inject constructor() {

    private val ref = FirebaseStorage.getInstance().reference

    suspend fun getQuotes(): Resource<List<FirebaseQuote>> {
        return try {
            val quotes = ref.child("quotes").listAll()
                .await()
                .items
                .map { FirebaseQuote(it.path) }
            Resource.success(quotes)
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }
}