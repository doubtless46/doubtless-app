package com.doubtless.doubtless.screens.inAppNotification.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.inAppNotification.dao.InAppNotificationDao
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkInAppNotificationsReadUseCase constructor(
    private val dao: InAppNotificationDao,
    private val firestore: FirebaseFirestore
) {

    sealed class Result {
        object Success : Result()
        class Error(val message: String) : Result()
    }

    suspend fun markRead(notifications: List<InAppNotificationEntity>): Result =
        withContext(Dispatchers.IO) {

            try {
                val markedReadNotifications = notifications.filter {
                    it.isRead == false
                }.map {
                    it.copy(isRead = true)
                }

                markReadOnFirestore(notifications)

                dao.insertNewNotifications(markedReadNotifications)

                return@withContext Result.Success

            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "some error occurred!")
            }

        }

    private fun markReadOnFirestore(notifications: List<InAppNotificationEntity>) {
        notifications.forEach {

            firestore.collection(FirestoreCollection.NOTIFICATION)
                .document(it.notificationId)
                .update("is_read", true)

        }
    }

}