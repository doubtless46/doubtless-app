package com.doubtless.doubtless.screens.inAppNotification.usecases

import com.doubtless.doubtless.screens.inAppNotification.dao.InAppNotificationDao
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchInAppNotificationUseCase constructor(
    private val inAppNotificationDao: InAppNotificationDao,
    private val unreadNotificationUseCase: FetchUnreadNotificationUseCase
) {
    sealed class Result {
        class Success(val notifications: List<InAppNotificationEntity>, val error: String? = null) : Result()
        class Error(val message: String): Result()
    }

    suspend fun getNotifications(): Result = withContext(Dispatchers.IO) {
        try {
            val notifications = mutableListOf<InAppNotificationEntity>()

            var error: String? = null

            // first fetch all unread notifications
            val result = unreadNotificationUseCase.fetchNotification()

            if (result is FetchUnreadNotificationUseCase.Result.Success) {
                notifications.addAll(result.notifications)
            }

            if (result is FetchUnreadNotificationUseCase.Result.Error) {
                error = result.message
            }

            // now fetch old read notifications from db
            try {
                notifications.addAll(inAppNotificationDao.getAllReadNotifications())
            } catch (e: Exception) {
                error = e.message
            }

            if (notifications.isEmpty() && error != null) {
                return@withContext Result.Error(error)
            }

            return@withContext Result.Success(notifications, error)

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "some error occurred!")
        }
    }

}