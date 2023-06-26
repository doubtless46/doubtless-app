package com.doubtless.doubtless.localDatabase

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.*

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun longToTimestamp(value: Long): Timestamp {
        return Timestamp(Date(value))
    }

    @TypeConverter
    fun timestampToLong(timestamp: Timestamp): Long {
        return timestamp.toDate().time
    }

}
