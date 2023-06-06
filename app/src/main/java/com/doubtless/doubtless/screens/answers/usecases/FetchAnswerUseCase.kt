package com.doubtless.doubtless.screens.answers.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.answers.AnswerData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchAnswerUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val doubtId: String
) {

    sealed class Result {
        class Success(val data: List<AnswerData>) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun fetchAnswers(): Result = withContext(Dispatchers.IO) {
        return@withContext try {

            val result = firestore.collection(FirestoreCollection.AllDoubts)
                .document(doubtId)
                .collection(FirestoreCollection.DoubtAnswer)
                .orderBy("net_votes", Query.Direction.DESCENDING)
                .get().await()

            Result.Success(AnswerData.parse(result)!!)

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred")
        }
    }

}