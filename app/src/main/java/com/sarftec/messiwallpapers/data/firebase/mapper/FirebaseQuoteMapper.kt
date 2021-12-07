package com.sarftec.messiwallpapers.data.firebase.mapper

import com.sarftec.messiwallpapers.data.firebase.model.FirebaseQuote
import com.sarftec.messiwallpapers.domain.model.MessiQuote
import javax.inject.Inject

class FirebaseQuoteMapper @Inject constructor(){

    fun toCR7Quote(quote: FirebaseQuote) : MessiQuote {
        return MessiQuote(quote.imagePath!!)
    }
}