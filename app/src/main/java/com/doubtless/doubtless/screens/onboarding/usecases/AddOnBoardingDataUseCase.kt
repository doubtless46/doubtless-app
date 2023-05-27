package com.doubtless.doubtless.screens.onboarding.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.UserAttributes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddOnBoardingDataUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val user: User
) {

    sealed class Result {
        class Success() : Result()
        class Error(val message: String) : Result()
    }

    suspend fun add(userAttributes: UserAttributes): Result = withContext(Dispatchers.IO) {

        try {
            val result = firestore.collection(FirestoreCollection.USER)
                .document(user.document_id!!)
                .collection(FirestoreCollection.USER_ATTR)
                .add(userAttributes)
                .await()

            if (result != null)
                return@withContext Result.Success()

            return@withContext Result.Error("some error occurred")

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "some error occurred")
        }

    }

}