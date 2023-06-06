package com.doubtless.doubtless.screens.search.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class FetchSearchResultsUseCase constructor(
    private val server: DoubtlessServer
) {

    sealed class Result {
        class Success(val searchResult: List<SearchResult>) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun getSearchResult(query: String): Result = withContext(Dispatchers.IO) {
        return@withContext try {

            var newQuery = ""

            newQuery = if (!query.last().isLetterOrDigit() && query.length > 1) {
                query.substring(0, query.length - 1)
            } else {
                query
            }

            val result = server.searchDoubts(newQuery).distinctBy {
                it.id
            }.sortedBy {
                ceil(it.netVotes)
            }

            Result.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message ?: "some error occurred")
        }

    }

}