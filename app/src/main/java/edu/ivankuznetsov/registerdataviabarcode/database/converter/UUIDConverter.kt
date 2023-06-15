package edu.ivankuznetsov.registerdataviabarcode.database.converter


import androidx.room.TypeConverter
import java.util.UUID


public class  UUIDConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun uuidFromString(string: String): UUID {
        return UUID.fromString(string)
    }
}
