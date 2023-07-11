package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.answers.AnswerData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchInteractedMentorDataForDoubt constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun fetch(doubtId: String): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {

            val docx = firestore.collection(FirestoreCollection.AllDoubts)
                .document(doubtId)
                .collection(FirestoreCollection.DoubtAnswer)
                .whereGreaterThanOrEqualTo("xp_count", 1000)
                .orderBy("xp_count", Query.Direction.DESCENDING)
                .limit(5)
                .get().await()

            val dps = AnswerData.parse(docx)?.map {
                it.authorPhotoUrl ?: ""
            }

            dps ?: listOf()

        } catch (e: Exception) {
            listOf()
        }
    }
}