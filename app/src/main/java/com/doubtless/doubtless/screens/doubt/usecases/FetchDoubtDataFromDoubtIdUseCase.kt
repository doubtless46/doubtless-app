package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchDoubtDataFromDoubtIdUseCase constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getDoubtData(doubtId: String): DoubtData? = withContext(Dispatchers.IO) {
        try {
            val document = firestore.collection(FirestoreCollection.AllDoubts)
                .whereEqualTo("doubt_id", doubtId)
                .get().await()

            return@withContext DoubtData.parse(document.documents[0])

        } catch (e: Exception) {
            return@withContext null
        }
    }

}