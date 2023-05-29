package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostDoubtUseCase constructor(
    private val server: DoubtlessServer,
    private val user: User
) {

    data class PostDoubtRequest(
        val author_id: String,
        val author_name: String,
        val author_photo_url: String,
        val author_college: String,
        val heading: String,
        val description: String,
        val net_votes: Float,
        val tags: List<String>,
        val keywords: List<String>
    )

    sealed class Result {
        object Success: Result()
        class Error(val message: String): Result()
    }

    suspend fun post(request: PostDoubtRequest): Result = withContext(Dispatchers.IO) {

        val tenPow6 = 1000000

        val request = request.copy(
            net_votes = (0..tenPow6).random().toFloat() / tenPow6
        ) // 0.(6 decimal places precision randomized)

        return@withContext Result.Success
    }

}