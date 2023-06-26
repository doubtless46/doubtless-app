package com.doubtless.doubtless.screens.inAppNotification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity

@Dao
interface InAppNotificationDao {

    @Query("SELECT * FROM InAppNotifications WHERE isRead = true")
    fun getAllReadNotifications(): List<InAppNotificationEntity>

    @Insert
    fun insertNewNotifications(notificationEntities: List<InAppNotificationEntity>)

}