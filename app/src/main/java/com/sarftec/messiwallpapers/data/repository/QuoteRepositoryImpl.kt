package com.sarftec.messiwallpapers.data.repository

import com.sarftec.messiwallpapers.data.firebase.mapper.FirebaseQuoteMapper
import com.sarftec.messiwallpapers.data.firebase.repository.FirebaseQuoteRepository
import com.sarftec.messiwallpapers.domain.model.MessiQuote
import com.sarftec.messiwallpapers.domain.repository.QuoteRepository
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val mapper: FirebaseQuoteMapper,
    private val firebaseQuoteRepository: FirebaseQuoteRepository
) : QuoteRepository {

    override suspend fun getMessiQuotes(): Resource<List<MessiQuote>> {
        return try {
            firebaseQuoteRepository.getQuotes().let { result ->
                if (result.isSuccess()) Resource.success(
                    result.data!!.map { mapper.toCR7Quote(it) }
                )
                else Resource.error("${result.message}")
            }
        } catch (e: Exception) {
            Resource.error(e.message)
        }
    }
}