package com.doubtless.doubtless.screens.inAppNotification.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchUnreadNotificationUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val userManager: UserManager
) {

    sealed class Result {
        class Success(val notifications: List<InAppNotificationEntity>) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun fetchNotification(): Result = withContext(Dispatchers.IO) {
        return@withContext try {

            val userId = userManager.getCachedUserData()!!.id

            val result = firestore.collection(FirestoreCollection.NOTIFICATION)
                .whereEqualTo("doubt_author_id", userId)
                .whereEqualTo("is_read", false)
                .get().await()

            val notifications = mutableListOf<InAppNotificationEntity>()

            result.documents.forEach {
                try {
                    notifications.add(InAppNotificationEntity.fromDocumentSnapshot(it)!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return@withContext Result.Success(notifications)

        } catch (e: Exception) {
            Result.Error(e.message ?: "some error occurred!")
        }
    }

}