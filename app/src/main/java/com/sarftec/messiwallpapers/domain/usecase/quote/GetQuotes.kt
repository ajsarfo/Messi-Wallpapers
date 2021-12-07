package com.sarftec.messiwallpapers.domain.usecase.quote

import com.sarftec.messiwallpapers.domain.model.MessiQuote
import com.sarftec.messiwallpapers.domain.repository.QuoteRepository
import com.sarftec.messiwallpapers.domain.usecase.UseCase
import com.sarftec.messiwallpapers.utils.Resource
import javax.inject.Inject

class GetQuotes @Inject constructor(
    private val repository: QuoteRepository
)
    : UseCase<GetQuotes.EmptyParam, GetQuotes.QuoteResult>() {

    override suspend fun execute(param: EmptyParam?): QuoteResult {
       val result = if(param == null) Resource.error("Error => GetQuotes param NULL!")
        else repository.getMessiQuotes()
        return QuoteResult(result)
    }

    object EmptyParam : Param
    class QuoteResult(val quotes: Resource<List<MessiQuote>>) : Response
}