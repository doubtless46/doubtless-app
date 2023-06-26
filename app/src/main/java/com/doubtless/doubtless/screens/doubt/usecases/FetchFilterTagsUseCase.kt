package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchFilterTagsUseCase constructor(
    private val firestore: FirebaseFirestore,
) {

    sealed class Result {
        class Success(val data: List<String>) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun fetchTagsFromFirebase(): Result = withContext(Dispatchers.IO) {
        return@withContext try {

            val tags: MutableList<String> = mutableListOf()

            val querySnapshot = firestore.collection(FirestoreCollection.MiscAppData)
                .whereEqualTo("type", "attr_data").get().await()

            for (document in querySnapshot.documents) {
                val tagsList = document.get("tags") as? List<String>
                tagsList?.let {
                    tags.addAll(it)
                }
            }

            Result.Success(tags)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch tags")
        }
    }

}
