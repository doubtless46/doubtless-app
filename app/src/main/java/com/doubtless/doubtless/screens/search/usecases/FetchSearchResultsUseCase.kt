package com.doubtless.doubtless.screens.search.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.doubt.DoubtData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FetchSearchResultsUseCase constructor(
    private val server: DoubtlessServer
) {

    sealed class Result {
        class Success(val searchResult: List<DoubtData>) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun getSearchResult(keywords: List<String>): Result = withContext(Dispatchers.IO) {

        val list = mutableListOf<DoubtData>()

        repeat(15) {
            if (keywords.isNotEmpty())
                list.add(
                    DoubtData(
                        id = "1audoausdouas",
                        userName = "Siddharth",
                        userId = "12830128301",
                        userPhotoUrl = null,
                        heading = keywords.toString(),
                        description = "I am siddharth sharma, And I need " + keywords.last(),
                        netVotes = (0..10).random().toFloat(),
                        no_answers = (0..20).random(),
                        date = Date()
                    )
                )
        }

        return@withContext Result.Success(list)
    }

}