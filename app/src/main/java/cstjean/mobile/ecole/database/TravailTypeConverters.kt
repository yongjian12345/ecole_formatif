package cstjean.mobile.ecole.database

import androidx.room.TypeConverter
import java.util.Date

class TravailTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
    @TypeConverter
    fun toDatE(milisSinceEpoch: Long): Date {
        return Date(milisSinceEpoch)
    }
}