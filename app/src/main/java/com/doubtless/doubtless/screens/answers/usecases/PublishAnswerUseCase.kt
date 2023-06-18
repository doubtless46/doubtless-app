package com.doubtless.doubtless.screens.answers.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.answers.PublishAnswerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublishAnswerUseCase constructor(
    private val doubtlessServer: DoubtlessServer
) {

    sealed class Result {
        class Success(val answerData: AnswerData): Result()
        class Error(val message: String): Result()
        object Loading: Result()
    }

    suspend fun publish(publishAnswerRequest: PublishAnswerRequest) = withContext(Dispatchers.IO) {
        // centralise error handling
        return@withContext try {
            val response = doubtlessServer.publishAnswer(publishAnswerRequest)

            Result.Success(response.toAnswerData())

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred")
        }
    }

}