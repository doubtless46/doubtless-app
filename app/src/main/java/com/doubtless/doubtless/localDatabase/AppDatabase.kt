package com.doubtless.doubtless.localDatabase

import androidx.room.*
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.screens.inAppNotification.dao.InAppNotificationDao
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity

class AppDatabase {
    @Database(entities = [InAppNotificationEntity::class], version = 1)
    @TypeConverters(Converters::class)
    abstract class AppDB : RoomDatabase() {
        abstract fun inAppNotificationDao(): InAppNotificationDao
    }

    companion object {

        private var database: AppDB? = null

        @Synchronized
        fun getDbInstance(): AppDB {

            if (database == null) {
                database = Room.databaseBuilder(
                    context = DoubtlessApp.getInstance(),
                    klass = AppDB::class.java,
                    name = "doubtless-db"
                ).build()
            }

            return database!!
        }
    }
}