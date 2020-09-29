package mx.com.example.bebidas.data.internal

import androidx.room.TypeConverter
import java.util.*


class DrinkTypeConverters {
    @TypeConverter
    fun calendarToLong(cal: Calendar?): Long?
        = cal?.timeInMillis

    @TypeConverter
    fun longToCalendar(long: Long?): Calendar?
        = long?.let {
            Calendar.getInstance().apply { timeInMillis = long }
        }
}