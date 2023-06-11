package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.doubt.PublishDoubtRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

            val response = server.publishDoubt(_request)

            if (response.errorBody() != null && !response.isSuccessful) {

                // {"message":"ASK QUESTIONS ONLY"}

                var json = Gson().toJson(response.errorBody()!!.string())
                json = json.replace("\\\"", "'")
                val jo = JSONObject(json.substring(1, json.length - 1))

                val errorMessage = jo.getString("message")
                throw Exception(errorMessage)
            }

            Result.Success

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred")
        }
    }

}