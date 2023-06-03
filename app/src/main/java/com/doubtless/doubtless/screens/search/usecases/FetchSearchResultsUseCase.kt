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

    suspend fun getSearchResult(query: String): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = server.searchDoubts(query)
            Result.Success(listOf())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "some error occurred")
        }

    }

}