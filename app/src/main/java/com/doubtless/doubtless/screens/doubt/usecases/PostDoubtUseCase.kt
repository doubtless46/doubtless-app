package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.PublishDoubtRequest
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostDoubtUseCase constructor(
    private val server: DoubtlessServer
) {

    sealed class Result {
        object Success : Result()
        class Error(val message: String) : Result()
    }

    suspend fun post(request: PublishDoubtRequest): Result = withContext(Dispatchers.IO) {

        return@withContext try {
            val tenPow6 = 1000000

            val _request = request.copy(
                netVotes = (0..tenPow6).random().toFloat() / tenPow6
            ) // 0.(6 decimal places precision randomized)

            server.publishDoubt(_request)

            Result.Success

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred")
        }
    }

}