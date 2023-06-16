package com.doubtless.doubtless.screens.answers.usecases

import com.doubtless.doubtless.network.DoubtlessServer
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.answers.PublishAnswerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublishAnswerUseCase constructor(
    private val doubtlessServer: DoubtlessServer
) {

    interface PublishAnswerListener {
        fun onPublishAnswerLoading(isLoading: Boolean)
    }

    sealed class Result {
        class Success(val answerData: AnswerData): Result()
        class Error(val message: String): Result()
    }

    sealed class Resource<out T> {
        data class Loading<out T>(val data: T? = null) : Resource<T>()
        data class Success<out T>(val data: T) : Resource<T>()
        data class Error(val message: String) : Resource<Nothing>()
    }

    suspend fun publish(publishAnswerRequest: PublishAnswerRequest,
                        listener: PublishAnswerListener? = null) = withContext(Dispatchers.IO) {
        // centralise error handling
        return@withContext try {

            val response = doubtlessServer.publishAnswer(publishAnswerRequest)
            Result.Success(response.toAnswerData())

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred")
        } finally {
            listener?.onPublishAnswerLoading(false)
        }
    }

}