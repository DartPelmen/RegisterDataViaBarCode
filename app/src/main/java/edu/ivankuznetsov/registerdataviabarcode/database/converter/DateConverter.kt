package edu.ivankuznetsov.registerdataviabarcode.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAccessor
import java.util.Date


public class DateConverter{
    @TypeConverter
    fun timestampFromDate(date: LocalDateTime): Long {
        return date.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun dateFromTimestamp(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(timestamp,0, ZoneOffset.UTC)
    }
}