package com.doubtless.doubtless.screens.onboarding.usecases

import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.onboarding.OnBoardingAttributes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchOnBoardingDataUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val user: User
) {

    sealed class Result {
        class Success(val data: OnBoardingAttributes) : Result()
        class Error(val error: String) : Result()
    }

    suspend fun getData(): Result = withContext(Dispatchers.IO) {
        try {
            val result = firestore.collection(FirestoreCollection.MiscAppData)
                .whereEqualTo("type", "attr_data")
                .get().await()

            if (result.documents.isEmpty())
                return@withContext Result.Error("No data present on server!")

            val onBoardingAttributes = OnBoardingAttributes.parse(result.documents[0])
                ?: return@withContext Result.Error("error parsing on-boarding data")

            return@withContext Result.Success(onBoardingAttributes)

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "some error occurred!")
        }
    }

}